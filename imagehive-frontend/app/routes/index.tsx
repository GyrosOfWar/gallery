import {json} from "@remix-run/node"
import {Link, useLoaderData} from "@remix-run/react"
import type {LoaderFunction} from "react-router"
import type {PageImageDTO} from "imagehive-client"
import {DefaultApi} from "imagehive-client"
import type {User} from "~/services/auth.server"
import {requireUser} from "~/services/auth.server"
import {PlusIcon} from "@heroicons/react/24/outline"

interface Data {
  user: User
  images: PageImageDTO | Array<PageImageDTO>
}

export const loader: LoaderFunction = async ({request}) => {
  const user = await requireUser(request)
  const api = new DefaultApi()
  let images
  try {
    images = await api.getImages(
      {
        pageable: {
          size: 20,
          sort: {
            orderBy: [],
          },
          orderBy: [],
        },
      },
      {
        headers: {
          authorization: `Bearer ${user.accessToken}`,
        },
      }
    )
    // workaround for empty array being returned for an empty page (meh)
  } catch (e) {
    images = []
  }
  const data = {user, images} satisfies Data
  return json(data)
}

export default function Index() {
  const {images, user} = useLoaderData<Data>()

  return (
    <>
      <p>Welcome, {user.username}</p>

      <div className="grid grid-cols-3">
        {Array.isArray(images) && <div>No photos yet!</div>}
        {!Array.isArray(images) &&
          images.content.map((image) => (
            <article key={image.id}>
              <Link to={`/image/${image.id}`}>
                <img alt={image.title || "<no title>"} src={`/api/media/${image.id}`} />
              </Link>
            </article>
          ))}

        <Link
          to="/upload"
          title="Upload new photos"
          className="fixed bottom-8 right-8 bg-gray-100 rounded-full shadow-xl hover:bg-gray-200 transition p-2"
        >
          <PlusIcon className="w-8 h-8" />
        </Link>
      </div>
    </>
  )
}
