import {useLoaderData} from "@remix-run/react"
import type {ImageDTO} from "imagehive-client"
import {DefaultApi} from "imagehive-client"
import type {LoaderFunction} from "react-router"
import {json} from "react-router"
import {requireUser} from "~/services/auth.server"

interface Data {
  data: ImageDTO
}

export const loader: LoaderFunction = async ({params, request}) => {
  const {accessToken} = await requireUser(request)

  const {uuid} = params
  if (!uuid) {
    return new Response("missing parameter", {status: 404})
  }
  const client = new DefaultApi()
  const data = await client.getImage(
    {uuid},
    {headers: {authorization: `Bearer ${accessToken}`}}
  )
  return json({data} satisfies Data)
}

const ImageDetailsPage: React.FC = () => {
  const {data} = useLoaderData<Data>()

  return (
    <div>
      <pre>{JSON.stringify(data, null, 2)}</pre>
    </div>
  )
}

export default ImageDetailsPage
