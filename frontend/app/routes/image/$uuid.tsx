import {Form, Link, useLoaderData} from "@remix-run/react"
import type {ImageDTO, ImageMetadata} from "imagehive-client"
import type {LoaderFunction} from "react-router"
import {json} from "react-router"
import {requireUser} from "~/services/auth.server"
import {originalImageUrl} from "~/util/consts"
import {formatRelative, parseISO} from "date-fns"
import {useMemo, useState} from "react"
import {ClientOnly} from "remix-utils"
import {
  CalendarIcon,
  CameraIcon,
  CheckIcon,
  PencilIcon,
  PhotoIcon,
  TagIcon,
} from "@heroicons/react/24/outline"
import {Button} from "flowbite-react"
import OpenStreetMapEmbed from "~/components/OpenStreetMapEmbed.client"
import http from "~/util/http"
import type {ActionFunction} from "@remix-run/node"
import ToggleableInput from "~/components/ToggleableInput"

const RelativeDate: React.FC<{timestamp: string | null | undefined}> = ({
  timestamp,
}) => {
  const formatted = useMemo(() => {
    return timestamp && formatRelative(parseISO(timestamp), new Date())
  }, [timestamp])

  if (!timestamp) {
    return <span>No date found</span>
  }

  return <time>{formatted}</time>
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
  const data: ImageDTO = await http.getJson(`/api/images/${uuid}`, accessToken)
  return json({data} satisfies Data)
}

export const action: ActionFunction = async ({request}) => {
  const data = await request.formData()
  const {accessToken} = await requireUser(request)

  const payload = Object.fromEntries(data.entries())
  return await http.patchJson("/api/images", payload, accessToken)
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

const Tags: React.FC<{tags: string[]}> = ({tags}) => {
  return (
    <ul className="flex gap-1">
      {tags.map((tag) => (
        <li
          className="bg-gray-100 dark:bg-gray-800 px-2 py-0.5 rounded-lg hover:underline"
          key={tag}
        >
          <Link
            to={{pathname: "/", search: `?query=${encodeURIComponent(tag)}`}}
          >
            {tag}
          </Link>
        </li>
      ))}
    </ul>
  )
}

const ImageDetailsPage: React.FC = () => {
  const {data} = useLoaderData<Data>()
  const [editMode, setEditMode] = useState(false)
  const toggleEditMode = () => {
    setEditMode((mode) => !mode)
  }

  return (
    <>
      <img
        src={originalImageUrl(data.id, data.extension)}
        alt={data.title || "no title"}
        className="max-h-[90vh] self-center"
      />

      <Form onSubmit={toggleEditMode} method="post">
        <input type="hidden" name="uuid" value={data.id} />
        <ul className="flex flex-col gap-4 my-4">
          <li className="flex gap-4 items-center">
            <PhotoIcon className="w-8 h-8" />
            <div className="flex flex-col gap-4">
              <ToggleableInput
                editMode={editMode}
                defaultValue={data.title || ""}
                name="title"
                placeholder="Title"
              />
              {(data.description || editMode) && (
                <ToggleableInput
                  editMode={editMode}
                  defaultValue={data.description || ""}
                  name="description"
                  placeholder="Description"
                />
              )}
            </div>
          </li>
          {data.tags && (
            <li className="flex gap-4 items-center">
              <TagIcon className="w-8 h-8" />
              <Tags tags={data.tags} />
            </li>
          )}

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
          <div className="mt-1">
            {!editMode && (
              <Button onClick={toggleEditMode} color="info">
                <PencilIcon className="w-4 h-4 mr-2" />
                Edit
              </Button>
            )}
            {editMode && (
              <Button type="submit" color="success">
                <CheckIcon className="w-4 h-4 mr-2" />
                Save
              </Button>
            )}
          </div>
        </ul>
      </Form>
      {data.latitude && data.longitude && (
        <ClientOnly>
          {() => (
            <OpenStreetMapEmbed
              lat={data.latitude!}
              lon={data.longitude!}
              name={data.title}
            />
          )}
        </ClientOnly>
      )}
    </>
  )
}

export default ImageDetailsPage
