import {Form, Link, useLoaderData} from "@remix-run/react"
import type {ImageDTO, ImageMetadata} from "imagehive-client"
import type {LoaderFunction} from "react-router"
import {json} from "react-router"
import {requireUser} from "~/services/auth.server"
import {originalImageUrl} from "~/util/consts"
import {formatRelative, parseISO} from "date-fns"
import {useMemo, useState} from "react"
import {ClientOnly} from "remix-utils"
import {Button} from "flowbite-react"
import OpenStreetMapEmbed from "~/components/OpenStreetMapEmbed.client"
import http from "~/util/http"
import type {ActionFunction} from "@remix-run/node"
import ToggleableInput from "~/components/ToggleableInput"
import {
  HiPhoto,
  HiTag,
  HiCalendar,
  HiCamera,
  HiCheck,
  HiStar,
  HiPencil,
  HiArrowDownTray,
  HiTrash,
  HiMapPin,
  HiShare,
} from "react-icons/hi2"
import useToggleFavorite from "~/hooks/useToggleFavorite"
import produce from "immer"

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
  const {data: initial} = useLoaderData<Data>()
  const [image, setImage] = useState(initial)
  const [editMode, setEditMode] = useState(false)
  const [sharing, setSharing] = useState(false)

  const toggleEditMode = () => {
    setEditMode((mode) => !mode)
  }
  const toggleFavorite = useToggleFavorite(image.id, (newImage) =>
    setImage(
      produce(image, (draft) => {
        draft.favorite = newImage.favorite
      })
    )
  )

  const deleteImage = () => {
    // todo
  }

  const shareImage = async () => {
    setSharing(true)
    const response = await fetch(originalImageUrl(image.id, image.extension))
    const blob = await response.blob()
    const file = new File([blob], `shared.${image.extension}`, {
      type: "image/jpeg",
      lastModified: new Date().getTime(),
    })

    await navigator.share({
      title: image.title || "Shared image",
      files: [file],
    })

    setSharing(false)
  }

  return (
    <>
      <img
        className="max-h-[90vh] flex self-center"
        src={originalImageUrl(image.id, image.extension)}
        alt={image.title || "no title"}
      />

      <div className="my-4 grid self-center md:self-start grid-cols-2 md:grid-cols-4 gap-1 w-fit">
        <Button onClick={shareImage} disabled={sharing}>
          <HiShare className="w-f h-4 mr-2" />
          Share
        </Button>
        <Button
          color="success"
          href={originalImageUrl(image.id, image.extension, true)}
        >
          <HiArrowDownTray className="w-4 h-4 mr-2" />
          Download
        </Button>

        <Button onClick={toggleFavorite}>
          <HiStar className="w-4 h-4 mr-2" />
          {image.favorite ? "Unfavorite" : "Favorite"}
        </Button>

        <Button color="failure" onClick={deleteImage}>
          <HiTrash className="w-4 h-4 mr-2" />
          Delete
        </Button>
      </div>

      <Form onSubmit={toggleEditMode} method="post" className="max-w-2xl">
        <input type="hidden" name="uuid" value={image.id} />
        <ul className="flex w-full flex-col gap-4 my-4">
          <li className="flex gap-4 items-center">
            <HiPhoto className="w-8 h-8" />
            <div className="flex flex-col grow">
              <ToggleableInput
                editMode={editMode}
                defaultValue={image.title || ""}
                name="title"
                placeholder="Title"
                className="font-semibold"
              />
              {(image.description || editMode) && (
                <ToggleableInput
                  editMode={editMode}
                  defaultValue={image.description || ""}
                  name="description"
                  placeholder="Description"
                  className="text-gray-600 dark:text-gray-200 font-light"
                />
              )}
            </div>
          </li>
          {image.tags && (
            <li className="flex gap-4 items-center">
              <HiTag className="w-8 h-8" />
              <Tags tags={image.tags} />
            </li>
          )}

          <li className="flex gap-4 items-center">
            <HiCalendar className="w-8 h-8" />
            <RelativeDate timestamp={image.capturedOn} />
          </li>
          {image.metadata && (
            <li className="flex gap-4 items-center">
              <HiCamera className="w-8 h-8" />
              <FormattedMetadata
                meta={image.metadata}
                width={image.width}
                height={image.height}
              />
            </li>
          )}
          {image.location && (
            <li className="flex gap-4 items-center">
              <HiMapPin className="w-8 h-8" />
              <div className="flex flex-wrap gap-x-4 lg:gap-6 ">
                <span>{image.location.country}</span>
                <span>{image.location.city}</span>
                <span>{image.location.district}</span>
                <span>{image.location.locality}</span>
                <span>{image.location.street}</span>
              </div>
            </li>
          )}
          <div className="mt-1">
            {!editMode && (
              <Button onClick={toggleEditMode} color="info">
                <HiPencil className="w-4 h-4 mr-2" />
                Edit
              </Button>
            )}
            {editMode && (
              <Button type="submit" color="success">
                <HiCheck className="w-4 h-4 mr-2" />
                Save
              </Button>
            )}
          </div>
        </ul>
      </Form>
      {image.latitude && image.longitude && (
        <ClientOnly>
          {() => (
            <OpenStreetMapEmbed
              lat={image.latitude!}
              lon={image.longitude!}
              name={image.title}
            />
          )}
        </ClientOnly>
      )}
    </>
  )
}

export default ImageDetailsPage
