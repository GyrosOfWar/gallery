import {json} from "@remix-run/node"
import {Form, Link, useLoaderData, useSearchParams} from "@remix-run/react"
import type {LoaderFunction} from "react-router"
import type {ImageDTO} from "imagehive-client"
import {requireUser} from "~/services/auth.server"
import {MagnifyingGlassIcon, PlusIcon} from "@heroicons/react/24/outline"
import {Masonry, useInfiniteLoader} from "masonic"
import {thumbnailUrl} from "~/util/consts"
import {Button, TextInput} from "flowbite-react"
import QueryStringHelper from "~/util/query-string-helper"
import {useState} from "react"
import http from "~/util/http"

interface Data {
  query?: string
  images: ImageDTO[]
}

export const loader: LoaderFunction = async ({request}) => {
  const user = await requireUser(request)
  const queryString = new QueryStringHelper(request.url)
  const query = queryString.getString("query", "")
  const params = new URL(request.url).searchParams
  const url = `/api/images?${params.toString()}`
  const images: ImageDTO[] = await http.getJson(url, user.accessToken)
  const data = {query, images} satisfies Data
  return json(data)
}

export default function Index() {
  const [searchParams] = useSearchParams()
  const {images: imagesInitial, query} = useLoaderData<Data>()
  const [images, setImages] = useState(imagesInitial)

  const fetchMoreItems = async () => {
    const params = new URLSearchParams()
    if (query) {
      params.set("query", query)
    }
    // TODO
    const nextPage = parseInt(searchParams.get("page") || "0") + 1
    params.set("page", nextPage.toString())
    // const response: ImageDTO[] = await http.getJson("/")
    // setImages([...images, response])
  }

  const maybeLoadMore = useInfiniteLoader(fetchMoreItems, {
    isItemLoaded: (index, items) => !!items[index],
  })

  const noImages = images.length === 0 && !query
  const nothingForQuery = images.length === 0 && query && query.length > 0

  return (
    <div className="relative flex flex-col grow">
      {!noImages && (
        <Form className="my-4 flex" method="get">
          <TextInput
            className="mr-2 grow"
            name="query"
            placeholder="Search..."
            defaultValue={query}
          />
          <Button type="submit">
            <MagnifyingGlassIcon className="w-4 h-4 mr-2" />
            Search
          </Button>
        </Form>
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

      <Masonry
        columnCount={4}
        columnGutter={4}
        items={images}
        key={query}
        itemKey={(image) => image.id}
        onRender={maybeLoadMore}
        render={(image) => (
          <Link to={`/image/${image.data.id}`}>
            <img
              alt={image.data.title || "<no title>"}
              src={thumbnailUrl(image.data.id, 600, 600, image.data.extension)}
            />
          </Link>
        )}
      />

      <Link
        to="/upload"
        title="Upload new photos"
        className="fixed bottom-8 right-8 bg-gray-100 rounded-full shadow-xl hover:bg-gray-200 transition p-4"
      >
        <PlusIcon className="w-10 h-10" />
      </Link>
    </div>
  )
}