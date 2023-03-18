import {Link} from "@remix-run/react"
import clsx from "clsx"
import type {ImageDTO} from "imagehive-client"
import useToggleFavorite from "~/hooks/useToggleFavorite"
import type {ClientImage} from "~/routes"
import {thumbnailUrl} from "~/util/consts"
import {HiStar} from "react-icons/hi"

export type ImageSize = "sm" | "md" | "lg" | "xl"

export interface Props {
  image: ClientImage
  size: ImageSize
  onImageFavorited?: (image: ImageDTO) => void
}

function getResolution(sizeType: ImageSize): number {
  switch (sizeType) {
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

export function getImageSize(
  sizeType: ImageSize,
  originalWidth: number,
  originalHeight: number
): [number, number] {
  const resolution = getResolution(sizeType)
  const aspectRatio = originalWidth / originalHeight
  const w = resolution
  const h = Math.round(w * (1.0 / aspectRatio))
  return [w, h]
}

const Overlay: React.FC<Pick<Props, "image" | "onImageFavorited">> = ({
  image,
  onImageFavorited,
}) => {
  const toggleFavorite = useToggleFavorite(image.id, onImageFavorited)

  return (
    <HiStar
      onClick={toggleFavorite}
      data-testid={`favorite-button-${image.id}`}
      className={clsx(
        "w-10 h-10 absolute bottom-1 right-1 text-yellow-300 hover:text-yellow-200",
        image.favorite && "fill-yellow-300"
      )}
    />
  )
}

const ThumbnailImage: React.FC<Props> = ({image, size, onImageFavorited}) => {
  const [width, height] = getImageSize(size, image.width, image.height)

  return (
    <Link
      className={clsx("mb-1 flex relative", size === "xl" && "justify-center")}
      to={`/image/${image.id}`}
      data-testid={`image-${image.id}`}
    >
      <Overlay image={image} onImageFavorited={onImageFavorited} />
      <img
        className="w-full"
        alt={image.title || "<no title>"}
        src={thumbnailUrl(image.id, width, height, image.extension)}
        height={width}
        width={height}
      />
    </Link>
  )
}

export default ThumbnailImage
