import type {ActionFunction, LoaderFunction} from "@remix-run/node"
import {redirect} from "@remix-run/node"
import {Form, useLoaderData} from "@remix-run/react"
import clsx from "clsx"
import {Button, Checkbox} from "flowbite-react"
import type {ImageDTO, PageImageDTO} from "imagehive-client"
import {useState} from "react"
import {HiCheck} from "react-icons/hi"
import {json} from "react-router"
import ImageGrid from "~/components/ImageGrid"
import {requireUser} from "~/services/auth.server"
import http from "~/util/http"

interface Data {
  albumImages: ImageDTO[]
  images: PageImageDTO
  albumId: string
}

interface OverlayProps {
  selected: boolean
  setSelected: (checked: boolean) => void
}

const Overlay: React.FC<OverlayProps> = ({selected, setSelected}) => {
  const onToggle = () => setSelected(!selected)

  return (
    <div
      onClick={onToggle}
      className={clsx("w-full h-full absolute cursor-pointer z-10 transition hover:bg-black hover:bg-opacity-25")}
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
  const data = useLoaderData<Data>()
  const [selection, setSelection] = useState(
    data.albumImages.map((image) => image.id)
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
        images={data.images.content}
        loading={false}
        hasNextPage={false}
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
