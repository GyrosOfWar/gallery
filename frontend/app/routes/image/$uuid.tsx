import {useLoaderData} from "@remix-run/react"
import type {ImageDTO, ImageMetadata} from "imagehive-client"
import {DefaultApi} from "imagehive-client"
import type {LoaderFunction} from "react-router"
import {json} from "react-router"
import {requireUser} from "~/services/auth.server"
import {originalImageUrl} from "~/util/consts"
import {parseISO, formatRelative} from "date-fns"
import {useMemo, useState} from "react"
import {
  CalendarIcon,
  CameraIcon,
  CheckIcon,
  PencilIcon,
  PhotoIcon,
} from "@heroicons/react/24/outline"
import {Button, TextInput} from "flowbite-react"

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

const FormattedMetadata: React.FC<{
  meta: ImageMetadata
  width: number
  height: number
}> = ({meta, width, height}) => {
  return (
    <div>
      <p className="text-xl">{meta.camera}</p>
      <div className="flex flex-wrap gap-x-4 lg:gap-6 font-light text-gray-600 dark:text-gray-300">
        <span>{meta.aperture}</span>
        <span>{meta.focalLength}</span>
        <span>{meta.exposure}</span>
        <span>ISO {meta.iso}</span>
        <span>
          {width} x {height}
        </span>
      </div>
    </div>
  )
}

//TODO: find a nicer solution to prevent react-leaflet server side rendering

let MapContainer = false;
let TileLayer = false;
let Marker = false;
let Popup = false;

if (process.env.BROWSER) {
  MapContainer = require('react-leaflet').MapContainer;
  TileLayer = require('react-leaflet').TileLayer;
  Marker = require('react-leaflet').Marker;
  Popup = require('react-leaflet').Popup;
}

const OpenStreetMapEmbed: React.FC<{
  lat: number
  long: number
  name: string | null | undefined
}> = ({lat, long, name}) => {
  return (
    <MapContainer center={[lat, long]} zoom={13} scrollWheelZoom={false}>
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      <Marker position={[lat, long]}>
        <Popup>
          {name}
        </Popup>
      </Marker>
    </MapContainer>
  )
}

const ImageDetailsPage: React.FC = () => {
  const {data} = useLoaderData<Data>()
  const [editMode, setEditMode] = useState(false)

  const toggleEditMode = () => setEditMode((mode) => !mode)

  return (
    <>
      <img
        src={originalImageUrl(data.id, data.extension)}
        alt={data.title || "no title"}
        className="max-h-[90vh] self-center"
      />

      <div className="mt-4">
        <Button onClick={toggleEditMode} color={editMode ? "success" : "info"}>
          {!editMode && (
            <>
              <PencilIcon className="w-4 h-4 mr-2" /> Edit
            </>
          )}
          {editMode && (
            <>
              <CheckIcon className="w-4 h-4 mr-2" /> Save
            </>
          )}
        </Button>
      </div>

      <ul className="flex flex-col gap-4 my-4">
        <li className="flex gap-4 items-center">
          <PhotoIcon className="w-8 h-8" />
          <div>
            {editMode && (
              <TextInput sizing="sm" defaultValue={data.title || ""} />
            )}
            {!editMode && <p>{data.title}</p>}
            {data.description && (
              <p className="text-gray-600 dark:text-gray-300 font-light">
                {data.description}
              </p>
            )}
          </div>
        </li>

        <li className="flex gap-4 items-center">
          <CalendarIcon className="w-8 h-8" />
          <RelativeDate timestamp={data.capturedOn} />
        </li>
        {data.metadata && (
          <li className="flex gap-4 items-center">
            <CameraIcon className="w-8 h-8" />
            <FormattedMetadata
              meta={data.metadata}
              width={data.width}
              height={data.height}
            />
          </li>
        )}
        {data.latitude && data.longitude && (
          <OpenStreetMapEmbed
            lat={data.latitude}
            long={data.longitude}
            name={data.title}
          />         
        )}
      </ul>
    </>
  )
}

export default ImageDetailsPage
