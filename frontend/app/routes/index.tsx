import {json} from "@remix-run/node"
import {
  Link,
  useFetcher,
  useLoaderData,
  useSearchParams,
} from "@remix-run/react"
import type {LoaderFunction} from "react-router"
import type {ImageDTO, PageImageDTO} from "imagehive-client"
import {requireUser} from "~/services/auth.server"
import {Button, TextInput} from "flowbite-react"
import type {FormEvent} from "react"
import {useEffect, useState} from "react"
import http from "~/util/http"
import useInfiniteScroll from "react-infinite-scroll-hook"
import Slider from "~/components/Slider"
import {useLocalStorage} from "usehooks-ts"
import {produce} from "immer"
import {HiPlus, HiSearch} from "react-icons/hi"
import ImageGrid from "~/components/ImageGrid"

interface Data {
  images: PageImageDTO
}

type ClientImageList = ReturnType<
  typeof useLoaderData<Data>
>["images"]["content"]
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
  const images: PageImageDTO = await http.getJson(requestUrl, user.accessToken)

  const data = {images} satisfies Data
  return json(data)
}

export default function Index() {
  const [queryParams, setQueryParams] = useSearchParams()
  const [query, setQuery] = useState(queryParams.get("query") || "")
  const fetcher = useFetcher<Data>()
  const {images: initialPage} = useLoaderData<Data>()
  const [pages, setPages] = useState([initialPage])
  const page = pages[pages.length - 1]
  const loading = fetcher.state !== "idle"
  const total = page?.totalPages || 0
  const number = page?.pageNumber || 0
  const hasNextPage = number < total - 1
  const [numColumns, setNumColumns] = useLocalStorage(
    "image-library-columns",
    4
  )

  const loadMore = () => {
    const nextPage = (page.pageNumber || 0) + 1
    fetcher.load(`/?index&page=${nextPage}`)
  }

  const [sentryRef] = useInfiniteScroll({
    loading,
    hasNextPage,
    onLoadMore: loadMore,
  })

  useEffect(() => {
    if (fetcher.data) {
      setPages((oldPages) => {
        if (fetcher.data && !fetcher.data.images.empty) {
          return [...oldPages, fetcher.data.images]
        } else {
          return oldPages
        }
      })
    }
  }, [fetcher.data])

  const onSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setQueryParams({query})
    setPages([])
    fetcher.load(`/?index&query=${encodeURIComponent(query)}`)
  }

  const onImageFavorited = (image: ImageDTO) => {
    setPages((pages) =>
      produce(pages, (draft) => {
        draft.forEach((page) => {
          page.content.forEach((img) => {
            if (img.id == image.id) {
              img.favorite = image.favorite
            }
          })
        })
      })
    )
  }

  const images = pages.flatMap((p) => p.content).filter(Boolean)
  const noImages = !query && page?.empty
  const nothingForQuery = query && page?.empty

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
              <HiSearch className="w-4 h-4 mr-2" />
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
            <p>No results for query '{query}'</p>
          </div>
        </div>
      )}
      <ImageGrid
        images={images}
        numColumns={numColumns}
        onImageFavorited={onImageFavorited}
        sentryRef={sentryRef}
        hasNextPage={hasNextPage}
        loading={loading}
        overlay="favorite"
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
