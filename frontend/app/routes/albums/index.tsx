import {PhotoIcon, PlusIcon} from "@heroicons/react/24/outline"
import type {LoaderFunction} from "@remix-run/node"
import {json} from "@remix-run/node"
import {Link, useLoaderData} from "@remix-run/react"
import type {AlbumListDTO} from "imagehive-client"
import Card from "~/components/Card"
import {requireUser} from "~/services/auth.server"
import {thumbnailUrl} from "~/util/consts"
import http from "~/util/http"

interface Data {
  albums: AlbumListDTO[]
}

export const loader: LoaderFunction = async ({request}) => {
  const {accessToken} = await requireUser(request)
  const albums: AlbumListDTO[] = await http.getJson("/api/albums", accessToken)
  return json({albums} satisfies Data)
}

// todo move to component
const buttonStyles =
  "px-4 py-2 text-white bg-blue-700 border border-transparent hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800 focus:!ring-2 group flex h-min items-center justify-center p-0.5 text-center font-medium focus:z-10 rounded-lg self-end"

const AlbumListPage: React.FC = () => {
  const {albums} = useLoaderData<Data>()

  return (
    <>
      <header className="flex justify-between items-center">
        <h1 className="text-3xl font-bold mb-4">Albums</h1>
        <Link to="/albums/create" className={buttonStyles}>
          <PlusIcon className="w-4 h-4 mr-2" /> New
        </Link>
      </header>
      <section className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4">
        {albums.map((album) => (
          <Link key={album.id} to={`/albums/${album.id}`}>
            <Card>
              {album.thumbnailImage && (
                <img
                  alt={album.name}
                  src={thumbnailUrl(album.thumbnailImage, 400, 400)}
                />
              )}
              {!album.thumbnailImage && (
                <PhotoIcon className="w-[400px] h-[250px] max-w-full max-h-full" />
              )}
              <p className="text-xl p-2">{album.name}</p>
            </Card>
          </Link>
        ))}
      </section>
    </>
  )
}

export default AlbumListPage
