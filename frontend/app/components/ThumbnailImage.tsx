import {Link} from "@remix-run/react"
import clsx from "clsx"
import type {ImageDTO} from "imagehive-client"
import type {ClientImage} from "~/routes"
import {thumbnailUrl} from "~/util/consts"

export type ImageSize = "sm" | "md" | "lg" | "xl"

export interface Props {
  image: ClientImage | ImageDTO
  size: ImageSize
  className?: string
  link?: string
  overlay?: React.ComponentClass
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

// const Overlay: React.FC<
//   Pick<Props, "image" | "onImageFavorited" | "overlay">
// > = ({image, onImageFavorited, overlay}) => {
//   const toggleFavorite = useToggleFavorite(image.id, onImageFavorited)

//   return (
//     <>
//       {overlay === "favorite" && (
//         <HiOutlineStar
//           onClick={toggleFavorite}
//           data-testid={`favorite-button-${image.id}`}
//           className={clsx(
//             "w-10 h-10 absolute bottom-1 right-1 text-yellow-300 hover:text-yellow-200",
//             image.favorite && "fill-yellow-300 hover:fill-yellow-200"
//           )}
//         />
//       )}
//       {overlay === "checkbox" && (
//         <Checkbox className="absolute bottom-2 right-2 w-6 h-6" />
//       )}
//     </>
//   )
// }

const ThumbnailImage: React.FC<Props> = ({
  image,
  size,
  overlay: Overlay,
  className,
  link,
}) => {
  const [width, height] = getImageSize(size, image.width, image.height)
  const children = (
    <>
      {Overlay && <Overlay />}
      <img
        className="w-full"
        alt={image.title || "<no title>"}
        src={thumbnailUrl(image.id, width, height, image.extension)}
        height={width}
        width={height}
      />
    </>
  )

  const classes = clsx(
    "mb-1 flex relative",
    size === "xl" && "justify-center",
    className
  )

  if (link) {
    return (
      <Link className={classes} to={link} data-testid={`image-${image.id}`}>
        {children}
      </Link>
    )
  } else {
    return <article className={classes}>{children}</article>
  }
}

export default ThumbnailImage
