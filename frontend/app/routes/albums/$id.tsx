import type {LoaderFunction} from "@remix-run/node"
import {json} from "@remix-run/node"
import {Link, useLoaderData} from "@remix-run/react"
import type {AlbumDetailsDTO, ImageDTO} from "imagehive-client"
import {requireUser} from "~/services/auth.server"
import http from "~/util/http"
import {HiPlus} from "react-icons/hi"
import ImageGrid from "~/components/ImageGrid"

interface Data {
  album: AlbumDetailsDTO
  images: ImageDTO[]
}

export const loader: LoaderFunction = async ({request, params}) => {
  const {accessToken} = await requireUser(request)
  if (!params.id) {
    return new Response("no album id parameter", {status: 400})
  }
  const album: AlbumDetailsDTO = await http.getJson(
    `/api/albums/${params.id}`,
    accessToken
  )
  const images: ImageDTO[] = await http.getJson(
    `/api/albums/${params.id}/images`,
    accessToken
  )
  return json({album, images} satisfies Data)
}

const AlbumDetailsPage = () => {
  const {album, images} = useLoaderData<Data>()

  return (
    <>
      <h1 className="text-3xl font-bold mb-4">{album.name}</h1>
      <Link
        to="add"
        title="Upload new photos"
        className="fixed bottom-8 right-8 bg-gray-100 dark:bg-gray-700 dark:hover:bg-gray-600 rounded-full shadow-xl hover:bg-gray-200 transition p-4"
      >
        <HiPlus className="w-10 h-10" />
      </Link>

      <ImageGrid withLinks images={images} hasNextPage={false} numColumns={4} />
    </>
  )
}

export default AlbumDetailsPage
