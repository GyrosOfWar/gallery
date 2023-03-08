import {StarIcon} from "@heroicons/react/24/outline"
import {Link} from "@remix-run/react"
import type {ClientImage} from "~/routes"
import {thumbnailUrl} from "~/util/consts"

const Overlay: React.FC = () => {
  const toggleFavorite = () => {
    // todo
  }

  return (
    <div className="z-10 absolute top-0 left-0 w-full h-full bg-transparent">
      <StarIcon
        onClick={toggleFavorite}
        color="yellow"
        className="w-12 h-12 absolute bottom-1 right-1"
      />
    </div>
  )
}

const ThumbnailImage: React.FC<{image: ClientImage}> = ({image}) => {
  return (
    <Link className="mb-1 flex relative block" to={`/image/${image.id}`}>
      <Overlay />
      <img
        alt={image.title || "<no title>"}
        src={thumbnailUrl(image.id, 600, 600, image.extension)}
      />
    </Link>
  )
}

export default ThumbnailImage
