import {expect, describe, it} from "vitest"
import {render, within} from "@testing-library/react"
import ThumbnailImage, {getImageSize} from "../ThumbnailImage"
import type {ClientImage} from "~/routes"
import {MemoryRouter} from "react-router"

const image = {
  createdOn: new Date().toISOString(),
  extension: "jpeg",
  favorite: false,
  height: 3000,
  width: 4000,
  id: "b349a127-a9f0-40d8-b37c-f5b5166efa7b",
} satisfies ClientImage

describe("ThumbnailImage", () => {
  it("should render an image with a link", () => {
    const {getByTestId} = render(
      <MemoryRouter>
        <ThumbnailImage link="/images/test" image={image} size="md" />
      </MemoryRouter>,
    )
    const node = getByTestId(`image-${image.id}`)
    const img = within(node).getByRole("img")
    expect(img).toBeInTheDocument()
    expect(img).toHaveAttribute("height")
    expect(img).toHaveAttribute("width")
    expect(img).toHaveAttribute("src")
    expect(node).toHaveAttribute("href", "/images/test")
  })
})

describe("getImageSize", () => {
  it("should correctly calculate the size of the image", () => {
    const [w, h] = getImageSize("xl", 4000, 3000)
    expect(w).toBeGreaterThan(h)
    expect(w / h).toBeCloseTo(4000 / 3000, 0.001)
  })
})
