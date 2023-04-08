import type {LoaderFunction} from "@remix-run/node"
import {json} from "@remix-run/node"
import {Link, useLoaderData} from "@remix-run/react"
import type {AlbumDetailsDTO, ImageDTO} from "imagehive-client"
import {requireUser} from "~/services/auth.server"
import http from "~/util/http"
import {HiPencil, HiPlus} from "react-icons/hi"
import ImageGrid from "~/components/ImageGrid"
import {Button} from "flowbite-react"

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
      <div className="flex justify-between">
        <h1 className="text-3xl font-bold mb-4">{album.name}</h1>
        <Link to={`/albums/${album.id}/edit`}>
          <Button color="success">
            <HiPencil className="w-4 h-4 mr-2" />
            Edit
          </Button>
        </Link>
      </div>

      <ImageGrid withLinks images={images} hasNextPage={false} numColumns={4} />
    </>
  )
}

export default AlbumDetailsPage
