import {StarIcon} from "@heroicons/react/24/outline"
import {Link} from "@remix-run/react"
import clsx from "clsx"
import type {ClientImage} from "~/routes"
import {thumbnailUrl} from "~/util/consts"

const Overlay: React.FC<{id: string; favorite: boolean}> = ({id, favorite}) => {
  const toggleFavorite = (event: React.MouseEvent) => {
    event.preventDefault()
    console.log("favoriting file", id)
  }

  return (
    <div className="z-10 absolute top-0 left-0 w-full h-full bg-transparent">
      <StarIcon
        onClick={toggleFavorite}
        className={clsx(
          "w-10 h-10 absolute bottom-1 right-1 text-yellow-300 hover:text-yellow-200",
          favorite && "fill-yellow-300"
        )}
      />
    </div>
  )
}

const ThumbnailImage: React.FC<{image: ClientImage}> = ({image}) => {
  return (
    <Link className="mb-1 flex relative" to={`/image/${image.id}`}>
      <Overlay id={image.id} favorite={image.favorite} />
      <img
        alt={image.title || "<no title>"}
        src={thumbnailUrl(image.id, 600, 600, image.extension)}
      />
    </Link>
  )
}

export default ThumbnailImage
