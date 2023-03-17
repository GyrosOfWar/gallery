import {StarIcon} from "@heroicons/react/24/outline"
import {Link} from "@remix-run/react"
import clsx from "clsx"
import type {ClientImage} from "~/routes"
import {thumbnailUrl} from "~/util/consts"

export interface Props {
  image: ClientImage
  imageRange: number
}

const Overlay: React.FC<Props> = ({image}) => {
  const toggleFavorite = async (event: React.MouseEvent) => {
    event.preventDefault()
  }

  return (
      <StarIcon
        onClick={toggleFavorite}
        className={clsx(
          "w-10 h-10 absolute bottom-1 right-1 text-yellow-300 hover:text-yellow-200",
          image.favorite && "fill-yellow-300"
        )}
      />
  )
}

const ThumbnailImage: React.FC<Props> = ({image, imageRange}) => {
  return (
    <Link className={`mb-1 flex relative ${imageRange === 1 ? 'justify-center' : ''}`} to={`/image/${image.id}`}>
      <Overlay image={image} />
      <img className=""
        alt={image.title || "<no title>"}
        src={thumbnailUrl(image.id, 1000, 1000, image.extension)}
      />

    </Link>
  )
}

export default ThumbnailImage
