/* eslint-disable react-hooks/exhaustive-deps */
import type {CSSProperties} from "react"
import {Children} from "react"
import type {ColumnCount} from "./ImageGrid"
import type {Device} from "~/services/device.server"

export interface RenderProps {
  style: CSSProperties
  className?: string
}

type RenderColumnFn = (
  content: React.ReactNode[],
  props: RenderProps,
  idx: number
) => React.ReactNode

export interface GridProps {
  className?: string
  columnClassName?: string
  renderColumn?: RenderColumnFn
  testId: string
  columnCount: ColumnCount
  device: Device
}

export function getColumnCountFromDevice(device: Device): number {
  switch (device) {
    case "pc":
      return 4
    case "phone":
      return 1
    case "tablet":
      return 2
  }
}

const Masonry: React.FC<React.PropsWithChildren<GridProps>> = ({
  className,
  columnClassName,
  children,
  renderColumn,
  testId,
  columnCount: columns,
  device,
}) => {
  const columnCount =
    columns === "auto" ? getColumnCountFromDevice(device) : columns

  const columnItems = (): React.ReactNode[][] => {
    const cols: React.ReactNode[][] = new Array(columnCount)
    const items = Children.toArray(children)
    for (let i = 0; i < items.length; i++) {
      const columnIndex = i % columnCount
      if (!cols[columnIndex]) {
        cols[columnIndex] = []
      }

      cols[columnIndex].push(items[i])
    }

    return cols
  }

  const renderColumnDefault: RenderColumnFn = (content, props, idx) => (
    <div key={idx} {...props}>
      {content}
    </div>
  )

  const renderColumns = () => {
    const cols = columnItems()
    const columnWidth = `${100 / cols.length}%`
    const columnProps = {
      style: {
        width: columnWidth,
      },
      className: columnClassName,
    }

    return cols.map((content, i) =>
      renderColumn
        ? renderColumn(content, columnProps, i)
        : renderColumnDefault(content, columnProps, i)
    )
  }

  return (
    <div className={className} data-testid={testId}>
      {renderColumns()}
    </div>
  )
}

export default Masonry
