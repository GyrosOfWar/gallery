import type {LoaderFunction} from "@remix-run/node"
import {useLoaderData} from "@remix-run/react"
import type {ImageDTO, PageImageDTO} from "imagehive-client"
import {json} from "react-router"
import ImageGrid from "~/components/ImageGrid"
import {requireUser} from "~/services/auth.server"
import http from "~/util/http"

// Needs: List of image IDs for the album
//        List of images (same as image list page)

interface Data {
  albumImages: ImageDTO[]
  images: PageImageDTO
  albumId: string
}

const Overlay = () => {
  return <div></div>
}

export const loader: LoaderFunction = async ({params, request}) => {
  const albumId = params.id as string
  const {accessToken} = await requireUser(request)
  const albumImages: ImageDTO[] = await http.getJson(
    `/api/albums/${albumId}/images`,
    accessToken
  )
  const images: PageImageDTO = await http.getJson(`/api/images`, accessToken)
  return json({
    albumImages,
    images,
    albumId,
  } satisfies Data)
}

const AddImagesPage: React.FC = () => {
  const data = useLoaderData<Data>()

  return (
    <>
      <h1 className="font-bold text-3xl mb-4">Add images to album</h1>

      <ImageGrid
        images={data.images.content}
        loading={false}
        hasNextPage={false}
        numColumns={4}
        imageOverlay={<Overlay />}
      />
    </>
  )
}

export default AddImagesPage
