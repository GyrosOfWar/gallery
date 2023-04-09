import type {ImageDTO} from "imagehive-client"
import type {ClientImage} from "~/routes"
import type {GridProps} from "./Masonry"
import {getColumnCountFromDevice} from "./Masonry"
import Masonry from "./Masonry"
import type {ImageSize} from "./ThumbnailImage"
import ThumbnailImage from "./ThumbnailImage"
import type {PropsWithChildren} from "react"
import clsx from "clsx"
import type {Device} from "~/services/device.server"

export type ColumnCount = number | "auto"

export interface Props {
  numColumns: ColumnCount
  images: (ImageDTO | ClientImage)[]
  hasNextPage: boolean
  loading?: boolean
  sentryRef?: React.Ref<HTMLDivElement>
  renderOverlay?: (image: ImageDTO | ClientImage) => React.ReactElement
  withLinks?: boolean
  square?: boolean
  device: Device
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

const SquareGrid: React.FC<PropsWithChildren<GridProps>> = ({
  children,
  columnCount,
  testId,
  className,
}) => {
  return (
    <div
      data-testid={testId}
      className={clsx(
        "grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-x-1",
        className
      )}
    >
      {children}
    </div>
  )
}

const ImageGrid: React.FC<Props> = ({
  numColumns,
  images,
  sentryRef,
  loading,
  hasNextPage,
  renderOverlay,
  withLinks,
  square,
  device,
}) => {
  const Component = square ? SquareGrid : Masonry
  const columnCount =
    numColumns === "auto" ? getColumnCountFromDevice(device) : numColumns

  return (
    <Component
      className="flex -ml-1"
      columnClassName="pl-1"
      testId="main-grid"
      columnCount={numColumns}
      device={device}
    >
      {images.map((image) => (
        <ThumbnailImage
          size={imageSizeForColumns(columnCount)}
          image={image}
          key={image.id}
          overlay={renderOverlay && renderOverlay(image)}
          link={withLinks ? `/image/${image.id}` : undefined}
          square={square}
        />
      ))}
      {(loading || hasNextPage) && sentryRef && (
        <div ref={sentryRef}>Loading...</div>
      )}
    </Component>
  )
}

export default ImageGrid
