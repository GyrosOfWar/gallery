import {json} from "@remix-run/node"
import {
  Link,
  useFetcher,
  useLoaderData,
  useSearchParams,
} from "@remix-run/react"
import type {LoaderFunction} from "react-router"
import type {PageImageDTO} from "imagehive-client"
import {requireUser} from "~/services/auth.server"
import {MagnifyingGlassIcon, PlusIcon} from "@heroicons/react/24/outline"
import {thumbnailUrl} from "~/util/consts"
import {Button, TextInput} from "flowbite-react"
import type {FormEvent} from "react"
import React, {useEffect, useState} from "react"
import http from "~/util/http"
import Masonry from "~/components/Masonry"
import useInfiniteScroll from "react-infinite-scroll-hook"

interface Data {
  images: PageImageDTO
}

type ClientImageList = ReturnType<
  typeof useLoaderData<Data>
>["images"]["content"]
type ClientImage = ClientImageList[0]

const Image: React.FC<{image: ClientImage}> = ({image}) => {
  return (
    <Link className="mb-1 flex" to={`/image/${image.id}`}>
      <img
        alt={image.title || "<no title>"}
        src={thumbnailUrl(image.id, 600, 600, image.extension)}
      />
    </Link>
  )
}

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
  const total = page.totalPages || 0
  const number = page.pageNumber || 0
  const hasNextPage = number < total - 1

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

  const images = pages.flatMap((p) => p.content).filter(Boolean)
  const noImages = !query && page.empty
  const nothingForQuery = query && page.empty

  return (
    <div className="relative flex flex-col grow">
      {!noImages && (
        <form onSubmit={onSubmit} className="my-4 flex" method="get">
          <TextInput
            className="mr-2 grow"
            name="query"
            placeholder="Search..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
          <Button type="submit">
            <MagnifyingGlassIcon className="w-4 h-4 mr-2" />
            Search
          </Button>
        </form>
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
        className="flex -ml-1"
        columnClassName="pl-1"
        columnCount={4}
        testId="main-grid"
      >
        {images.map((image) => (
          <Image image={image} key={image.id} />
        ))}
        {(loading || hasNextPage) && <div ref={sentryRef}>Loading...</div>}
      </Masonry>
      <Link
        to="/upload"
        title="Upload new photos"
        className="fixed bottom-8 right-8 bg-gray-100 dark:bg-gray-700 dark:hover:bg-gray-600 rounded-full shadow-xl hover:bg-gray-200 transition p-4"
      >
        <PlusIcon className="w-10 h-10" />
      </Link>
    </div>
  )
}
