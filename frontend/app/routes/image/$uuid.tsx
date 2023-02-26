import {useLoaderData} from "@remix-run/react"
import type {ImageDTO} from "imagehive-client"
import {DefaultApi} from "imagehive-client"
import type {LoaderFunction} from "react-router"
import {json} from "react-router"
import KVList from "~/components/KeyValueList"
import {requireUser} from "~/services/auth.server"
import {originalImageUrl} from "~/util/consts"
import {parseISO, formatRelative} from "date-fns"
import {useMemo} from "react"

const RelativeDate: React.FC<{timestamp: string | null | undefined}> = ({
  timestamp,
}) => {
  const formatted = useMemo(() => {
    return timestamp && formatRelative(parseISO(timestamp), new Date())
  }, [timestamp])

  if (!timestamp) {
    return <span>No date found</span>
  }

  return <time dateTime={timestamp}>{formatted}</time>
}

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
    <>
      <img
        src={originalImageUrl(data.id, data.extension)}
        alt={data.title || "no title"}
        className="max-h-[95vh] self-center"
      />
      <KVList>
        {data.title && (
          <KVList.Item>
            <KVList.Key>Title</KVList.Key>
            <KVList.Value>{data.title}</KVList.Value>
          </KVList.Item>
        )}

        {data.description && (
          <KVList.Item>
            <KVList.Key>Description</KVList.Key>
            <KVList.Value>{data.description}</KVList.Value>
          </KVList.Item>
        )}

        <KVList.Item>
          <KVList.Key>Date captured</KVList.Key>
          <KVList.Value>
            <RelativeDate timestamp={data.capturedOn} />
          </KVList.Value>
        </KVList.Item>
        <KVList.Item>
          <KVList.Key>Dimensions</KVList.Key>
          <KVList.Value>
            {data.width} x {data.height} px
          </KVList.Value>
        </KVList.Item>
      </KVList>
    </>
  )
}

export default ImageDetailsPage
