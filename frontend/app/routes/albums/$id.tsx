import type {LoaderFunction} from "@remix-run/node"
import {json} from "@remix-run/node"
import {Link, useLoaderData} from "@remix-run/react"
import type {AlbumDetailsDTO} from "imagehive-client"
import {requireUser} from "~/services/auth.server"
import http from "~/util/http"
import {HiPlus} from "react-icons/hi"

interface Data {
  album: AlbumDetailsDTO
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
  return json({album} satisfies Data)
}

const AlbumDetailsPage = () => {
  const {album} = useLoaderData<Data>()

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
    </>
  )
}

export default AlbumDetailsPage
