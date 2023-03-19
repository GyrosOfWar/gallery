import {LoaderFunction} from "@remix-run/node"
import {ImageDTO} from "imagehive-client"
import http from "~/util/http"

interface Data {
  existingImages: ImageDTO[]
}

export const loader: LoaderFunction = ({params}) => {
  const albumId = params.id as string
}

const AddImagesPage: React.FC = () => {
  return (
    <>
      <h1 className="font-bold text-3xl mb-4">Add images to album</h1>
    </>
  )
}

export default AddImagesPage
