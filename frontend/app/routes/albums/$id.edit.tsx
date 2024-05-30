import type {ActionFunction, LoaderFunction} from "@remix-run/node"
import {redirect} from "@remix-run/node"
import {Form, useLoaderData} from "@remix-run/react"
import {Button, Checkbox} from "flowbite-react"
import type {
  ImageDetailsDTO,
  ImageListDTO,
  PageImageListDTO,
} from "imagehive-client"
import {useState} from "react"
import {HiCheck} from "react-icons/hi"
import {json} from "react-router"
import ImageGrid from "~/components/ImageGrid"
import useDevice from "~/hooks/useDevice"
import useImages from "~/hooks/useImages"
import {requireUser} from "~/services/auth.server"
import http from "~/util/http"

interface Data {
  albumImages: ImageListDTO[]
  images: PageImageListDTO
  albumId: string
}

interface OverlayProps {
  selected: boolean
  setSelected: (checked: boolean) => void
}

const Overlay: React.FC<OverlayProps> = ({selected, setSelected}) => {
  const onToggle = () => setSelected(!selected)

  return (
    // FIXME
    // eslint-disable-next-line jsx-a11y/no-static-element-interactions, jsx-a11y/click-events-have-key-events
    <div
      onClick={onToggle}
      className="w-full h-full absolute cursor-pointer z-10 transition hover:bg-black hover:bg-opacity-25"
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
  const albumImages: ImageDetailsDTO[] = await http.getJson(
    `/api/albums/${albumId}/images`,
    accessToken,
  )
  const images: PageImageListDTO = await http.getJson(
    `/api/images`,
    accessToken,
  )
  return json({
    albumImages,
    images,
    albumId,
  } satisfies Data)
}

export const action: ActionFunction = async ({request, params}) => {
  const {accessToken} = await requireUser(request)
  const payload = await request.formData()
  const idList = payload.get("ids") as string
  const ids = idList.split(",")

  const albumId = params.id
  await http.postJson(`/api/albums/${albumId}/images`, ids, accessToken)

  return redirect(`/albums/${albumId}`)
}

const AddImagesPage: React.FC = () => {
  const device = useDevice()
  const data = useLoaderData<Data>()
  const props = useImages({initialPage: data.images})
  const [selection, setSelection] = useState(
    data.albumImages.map((image) => image.id),
  )

  return (
    <>
      <div className="flex justify-between">
        <h1 className="font-bold text-3xl mb-4">Edit album</h1>
        <Form method="post">
          <input type="hidden" name="ids" value={selection.join(",")} />
          <Button color="success" type="submit">
            <HiCheck className="w-4 h-4 mr-2" />
            Save
          </Button>
        </Form>
      </div>

      <ImageGrid
        {...props}
        device={device}
        square
        numColumns={4}
        renderOverlay={(image) => (
          <Overlay
            selected={selection.includes(image.id)}
            setSelected={(selected) => {
              if (selected) {
                setSelection((sel) => [...sel, image.id])
              } else {
                setSelection((sel) => sel.filter((id) => id !== image.id))
              }
            }}
          />
        )}
      />
    </>
  )
}

export default AddImagesPage
