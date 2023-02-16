import {json} from "@remix-run/node"
import {Link, useLoaderData} from "@remix-run/react"
import type {LoaderFunction} from "react-router"
import type {PageImageDTO} from "~/client"
import {DefaultApi} from "~/client"
import type {User} from "~/services/auth.server"
import {requireUser} from "~/services/auth.server"

interface Data {
  user: User
  images: PageImageDTO
}

export const loader: LoaderFunction = async ({request}) => {
  const user = await requireUser(request)
  const api = new DefaultApi()
  const images = await api.getImages(
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
        Authorization: `Bearer ${user.accessToken}`,
      },
    }
  )
  const data = {user, images} satisfies Data
  return json(data)
}

export default function Index() {
  const {images, user} = useLoaderData<Data>()

  return (
    <>
      <p>Welcome, {user.username}</p>

      <div className="grid grid-cols-3">
        {images.content.map((image) => (
          <article key={image.id}>
            <Link to={`/image/${image.id}`}>
              <img src={`/api/media/${image.id}`} />
            </Link>
          </article>
        ))}
      </div>
    </>
  )
}
