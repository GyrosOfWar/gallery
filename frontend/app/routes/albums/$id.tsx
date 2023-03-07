import type {LoaderFunction} from "@remix-run/node"
import {json} from "@remix-run/node"
import {useLoaderData} from "@remix-run/react"
import type {AlbumDetailsDTO} from "imagehive-client"
import {requireUser} from "~/services/auth.server"
import http from "~/util/http"

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
      <h1 className="text-4xl font-bold mb-4">{album.name}</h1>
    </>
  )
}

export default AlbumDetailsPage
