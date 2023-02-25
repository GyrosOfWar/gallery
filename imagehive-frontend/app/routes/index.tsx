import {json} from "@remix-run/node"
import {Form, Link, useFetcher, useLoaderData} from "@remix-run/react"
import type {LoaderFunction} from "react-router"
import type {ImageDTO, Pageable} from "imagehive-client"
import {DefaultApi} from "imagehive-client"
import {requireUser} from "~/services/auth.server"
import {MagnifyingGlassIcon, PlusIcon} from "@heroicons/react/24/outline"
import {Masonry, useInfiniteLoader} from "masonic"
import {thumbnailUrl} from "~/util/consts"
import {Button, TextInput} from "flowbite-react"
import QueryStringHelper from "~/util/query-string-helper"

interface Data {
  query?: string
  images: ImageDTO[]
  pageable: Pageable
}

export const loader: LoaderFunction = async ({request}) => {
  const user = await requireUser(request)
  const api = new DefaultApi()
  const queryString = new QueryStringHelper(request.url)
  const pageable = {
    size: queryString.getNumber("size", 10),
    orderBy: [],
    sort: {
      orderBy: [],
    },
    number: queryString.getNumber("page", 0),
  } satisfies Pageable
  const query = queryString.getString("query", "")

  const images = await api.getImages(
    {
      pageable,
      query,
    },
    {
      headers: {
        authorization: `Bearer ${user.accessToken}`,
      },
    }
  )
  const data = {query, images, pageable} satisfies Data
  return json(data)
}

export default function Index() {
  const {images, query, pageable} = useLoaderData<Data>()
  const fetcher = useFetcher()

  const fetchMoreItems = async () => {
    fetcher.submit(
      {page: ((pageable.number || 0) + 1).toString()},
      {method: "get"}
    )
  }
  const maybeLoadMore = useInfiniteLoader(fetchMoreItems, {
    isItemLoaded: (index, items) => !!items[index],
  })

  return (
    <div className="relative flex flex-col grow">
      {images.length === 0 && (
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
              </Link>{" "}
            </p>
          </div>
        </div>
      )}
      {images.length > 0 && !query && (
        <>
          <Form className="my-4 flex" method="get">
            <TextInput
              className="mr-2 grow"
              name="query"
              placeholder="Search..."
            />
            <Button type="submit">
              <MagnifyingGlassIcon className="w-4 h-4 mr-2" />
              Search
            </Button>
          </Form>
          <Masonry
            columnCount={4}
            columnGutter={4}
            items={images}
            key={query}
            onRender={maybeLoadMore}
            render={(image) => (
              <img
                alt={image.data.title || "<no title>"}
                src={thumbnailUrl(
                  image.data.id,
                  600,
                  600,
                  image.data.extension
                )}
              />
            )}
          />
        </>
      )}

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
