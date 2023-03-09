import {StarIcon} from "@heroicons/react/24/outline"
import {Link} from "@remix-run/react"
import clsx from "clsx"
import type {ClientImage} from "~/routes"
import {thumbnailUrl} from "~/util/consts"

export interface Props {
  image: ClientImage
}

const Overlay: React.FC<Props> = ({image}) => {
  const toggleFavorite = async (event: React.MouseEvent) => {
    event.preventDefault()
  }

  return (
    <div className="z-10 absolute top-0 left-0 w-full h-full bg-transparent">
      <StarIcon
        onClick={toggleFavorite}
        className={clsx(
          "w-10 h-10 absolute bottom-1 right-1 text-yellow-300 hover:text-yellow-200",
          image.favorite && "fill-yellow-300"
        )}
      />
    </div>
  )
}

const ThumbnailImage: React.FC<Props> = (props) => {
  const {image} = props
  return (
    <Link className="mb-1 flex relative" to={`/image/${image.id}`}>
      <Overlay {...props} />
      <img
        alt={image.title || "<no title>"}
        src={thumbnailUrl(image.id, 600, 600, image.extension)}
      />
    </Link>
  )
}

export default ThumbnailImage
