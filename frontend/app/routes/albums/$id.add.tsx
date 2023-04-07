import type {LoaderFunction} from "@remix-run/node"
import {useLoaderData} from "@remix-run/react"
import clsx from "clsx"
import {Checkbox} from "flowbite-react"
import type {ImageDTO, PageImageDTO} from "imagehive-client"
import {useState} from "react"
import {json} from "react-router"
import ImageGrid from "~/components/ImageGrid"
import {requireUser} from "~/services/auth.server"
import http from "~/util/http"

interface Data {
  albumImages: ImageDTO[]
  images: PageImageDTO
  albumId: string
}

const Overlay = () => {
  const [selected, setSelected] = useState(false)
  const onToggle = () => setSelected((s) => !s)

  return (
    <div
      onClick={onToggle}
      className={clsx("w-full h-full absolute cursor-pointer z-10")}
    >
      <Checkbox
        checked={selected}
        onChange={onToggle}
        className="absolute bottom-2 right-2 w-8 h-8 z-20"
      />
    </div>
  )
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
        renderOverlay={(image) => <Overlay />}
      />
    </>
  )
}

export default AddImagesPage
