import type {ImageDTO} from "imagehive-client"
import type {ClientImage} from "~/routes"
import Masonry from "./Masonry"
import type {ImageSize} from "./ThumbnailImage"
import ThumbnailImage from "./ThumbnailImage"

export interface Props {
  numColumns: number
  images: (ImageDTO | ClientImage)[]
  hasNextPage: boolean
  loading?: boolean
  sentryRef?: React.Ref<HTMLDivElement>
  renderOverlay?: (image: ImageDTO | ClientImage) => React.ReactElement
  withLinks?: boolean
}

function imageSizeForColumns(columns: number): ImageSize {
  if (columns === 1) {
    return "xl"
  } else if (columns === 2) {
    return "lg"
  } else if (columns <= 4) {
    return "md"
  } else {
    return "sm"
  }
}

const ImageGrid: React.FC<Props> = ({
  numColumns,
  images,
  sentryRef,
  loading,
  hasNextPage,
  renderOverlay,
  withLinks,
}) => {
  return (
    <Masonry
      className="flex -ml-1"
      columnClassName="pl-1"
      testId="main-grid"
      columnCount={numColumns}
    >
      {images.map((image) => (
        <ThumbnailImage
          size={imageSizeForColumns(numColumns)}
          image={image}
          key={image.id}
          overlay={renderOverlay && renderOverlay(image)}
          link={withLinks ? `/image/${image.id}` : undefined}
        />
      ))}
      {(loading || hasNextPage) && sentryRef && (
        <div ref={sentryRef}>Loading...</div>
      )}
    </Masonry>
  )
}

export default ImageGrid
