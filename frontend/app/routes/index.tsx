import {json} from "@remix-run/node"
import {
  Link,
  useFetcher,
  useLoaderData,
  useSearchParams,
} from "@remix-run/react"
import type {LoaderFunction} from "react-router"
import type {ImageListDTO, PageImageListDTO} from "imagehive-client"
import {requireUser} from "~/services/auth.server"
import {Button, TextInput} from "flowbite-react"
import type {FormEvent} from "react"
import {useState} from "react"
import http from "~/util/http"
import Slider from "~/components/Slider"
import {useLocalStorage} from "usehooks-ts"
import {produce} from "immer"
import {HiOutlineStar, HiPlus, HiMagnifyingGlass} from "react-icons/hi2"
import ImageGrid from "~/components/ImageGrid"
import useToggleFavorite from "~/hooks/useToggleFavorite"
import clsx from "clsx"
import useImages from "~/hooks/useImages"
import useDevice from "~/hooks/useDevice"

export interface Data {
  images: PageImageListDTO
}

export type ClientImagePage = ReturnType<typeof useLoaderData<Data>>["images"]
export type ClientImageList = ClientImagePage["content"]
export type ClientImage = ClientImageList[0]

export const loader: LoaderFunction = async ({request}) => {
  const user = await requireUser(request)
  const url = new URL(request.url)
  const query = url.searchParams
  if (!query.has("size")) {
    query.set("size", "20")
  }

  const queryString = query.get("query")
  if (typeof queryString === "string" && queryString.length === 0) {
    query.delete("query")
  }

  const requestUrl = `/api/images?${query.toString()}`
  const images: PageImageListDTO = await http.getJson(
    requestUrl,
    user.accessToken,
  )

  const data = {images} satisfies Data
  return json(data)
}

interface OverlayProps {
  image: ImageListDTO
  onImageFavorited: (image: ImageListDTO) => void
}

const Overlay: React.FC<OverlayProps> = ({image, onImageFavorited}) => {
  const toggleFavorite = useToggleFavorite(image.id, onImageFavorited)

  return (
    <HiOutlineStar
      onClick={toggleFavorite}
      data-testid={`favorite-button-${image.id}`}
      className={clsx(
        "w-10 h-10 absolute bottom-1 right-1 text-yellow-300 hover:text-yellow-200 opacity-75",
        image.favorite && "fill-yellow-300 hover:fill-yellow-200 opacity-100",
      )}
    />
  )
}

export default function Index() {
  const device = useDevice()
  const [queryParams, setQueryParams] = useSearchParams()
  const [query, setQuery] = useState(queryParams.get("query") || "")
  const fetcher = useFetcher<Data>()
  const {images: initialPage} = useLoaderData<Data>()
  const {setPages, images, loading, sentryRef, hasNextPage, lastPage} =
    useImages({
      initialPage,
    })
  const [numColumns, setNumColumns] = useLocalStorage(
    "image-library-columns",
    4,
  )

  const onSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setQueryParams({query})
    setPages([])
    fetcher.load(`/?index&query=${encodeURIComponent(query)}`)
  }

  const onImageFavorited = (image: ImageListDTO) => {
    setPages((pages) =>
      produce(pages, (draft) => {
        draft.forEach((page) => {
          page.content.forEach((img) => {
            if (img.id == image.id) {
              img.favorite = image.favorite
            }
          })
        })
      }),
    )
  }

  const noImages = !query && lastPage?.empty
  const nothingForQuery = query && lastPage?.empty

  return (
    <div className="relative flex flex-col grow">
      {!noImages && (
        <>
          <form
            onSubmit={onSubmit}
            className="my-4 flex gap-2 items-center"
            method="get"
          >
            <TextInput
              className="mr-2 grow"
              name="query"
              placeholder="Search..."
              value={query}
              onChange={(e) => setQuery(e.target.value)}
            />
            <Slider
              min={1}
              max={8}
              onChange={(e) => setNumColumns(e.target.valueAsNumber)}
              value={numColumns}
            />
            <Button type="submit">
              <HiMagnifyingGlass className="w-4 h-4 mr-2" />
              Search
            </Button>
          </form>
        </>
      )}
      {noImages && (
        <div className="grow flex justify-center items-center text-xl">
          <div className="text-center">
            <p>No photos yet!</p>
            <p>
              Upload some{" "}
              <Link
                className="text-blue-600 underline hover:text-blue-500"
                to="/upload"
              >
                here!
              </Link>
            </p>
          </div>
        </div>
      )}
      {nothingForQuery && (
        <div className="grow flex justify-center items-center text-xl">
          <div className="text-center">
            <p>No results for query &apos;{query}&apos;</p>
          </div>
        </div>
      )}
      <ImageGrid
        images={images}
        numColumns={numColumns}
        sentryRef={sentryRef}
        hasNextPage={hasNextPage}
        loading={loading}
        withLinks
        device={device}
        renderOverlay={(image) => (
          <Overlay
            image={image as ImageListDTO}
            onImageFavorited={onImageFavorited}
          />
        )}
      />

      <Link
        to="/upload"
        title="Upload new photos"
        className="fixed bottom-8 right-8 bg-gray-100 dark:bg-gray-700 dark:hover:bg-gray-600 rounded-full shadow-xl hover:bg-gray-200 transition p-4"
      >
        <HiPlus className="w-10 h-10" />
      </Link>
    </div>
  )
}
