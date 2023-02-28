import {json} from "@remix-run/node"
import {Form, Link, useLoaderData, useSearchParams} from "@remix-run/react"
import type {LoaderFunction} from "react-router"
import type {ImageDTO, PageImageDTO} from "imagehive-client"
import {requireUser} from "~/services/auth.server"
import {MagnifyingGlassIcon, PlusIcon} from "@heroicons/react/24/outline"
import {thumbnailUrl} from "~/util/consts"
import {Button, Pagination, TextInput} from "flowbite-react"
import React from "react"
import http from "~/util/http"

interface Data {
  images: ImageDTO[]
}

type ClientImageList = ReturnType<typeof useLoaderData<Data>>["images"]
type ClientImage = ClientImageList[0]

const Image: React.FC<{image: ClientImage}> = ({image}) => {
  return (
    <Link to={`/image/${image.id}`}>
      <img
        alt={image.title || "<no title>"}
        src={thumbnailUrl(image.id, 600, 600, image.extension)}
      />
    </Link>
  )
}

const SimpleGrid: React.FC<{images: ClientImageList}> = ({images}) => (
  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-1">
    {images.map((image) => (
      <Image image={image} key={image.id} />
    ))}
  </div>
)

export const loader: LoaderFunction = async ({request}) => {
  const user = await requireUser(request)
  const query = new URL(request.url).search
  const url = `/api/images${query}`

  const images: ImageDTO[] = await http.getJson(url, user.accessToken)
  console.log(images)
  const data = {images} satisfies Data
  return json(data)
}

export default function Index() {
  const [queryParams, setQueryParams] = useSearchParams()
  const query = queryParams.get("query") || ""
  const {images} = useLoaderData<Data>()
  const noImages = images.length === 0 && !query
  const nothingForQuery = images.length === 0 && query && query.length > 0
  const currentPage = parseInt(queryParams.get("page") || "0")
  const totalPages = 3

  const onPageChange = (page: number) => {
    setQueryParams({
      page: page.toString(),
    })
  }

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
      <SimpleGrid images={images} />
      <Pagination
        className="self-center"
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={onPageChange}
      />
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
