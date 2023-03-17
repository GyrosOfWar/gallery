import {StarIcon} from "@heroicons/react/24/outline"
import {Link} from "@remix-run/react"
import clsx from "clsx"
import type {ClientImage} from "~/routes"
import {thumbnailUrl} from "~/util/consts"

export type ImageSize = "sm" | "md" | "lg" | "xl"
export interface Props {
  image: ClientImage
  size: ImageSize
}

function getImageSize(size: ImageSize): number {
  switch (size) {
    case "sm":
      return 300
    case "md":
      return 500
    case "lg":
      return 800
    case "xl":
      return 1500
  }
}

const Overlay: React.FC<{image: ClientImage}> = ({image}) => {
  const toggleFavorite = async (event: React.MouseEvent) => {
    // TODO send request
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

const ThumbnailImage: React.FC<Props> = ({image, size}) => {
  const resolution = getImageSize(size)

  return (
    <Link
      className={clsx("mb-1 flex relative", size === "xl" && "justify-center")}
      to={`/image/${image.id}`}
    >
      <Overlay image={image} />
      <img
        className="w-full"
        alt={image.title || "<no title>"}
        src={thumbnailUrl(image.id, resolution, resolution, image.extension)}
        height={resolution}
        width={resolution}
      />
    </Link>
  )
}

export default ThumbnailImage
