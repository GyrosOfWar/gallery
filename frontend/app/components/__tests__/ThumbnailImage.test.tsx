import {expect, describe, it} from "vitest"
import {render, within} from "@testing-library/react"
import ThumbnailImage from "../ThumbnailImage"
import type {ClientImage} from "~/routes"
import {MemoryRouter} from "react-router"

describe("ThumbnailImage", () => {
  it("should render an image with a link", () => {
    const image = {
      createdOn: new Date().toISOString(),
      extension: ".jpg",
      favorite: false,
      height: 3200,
      width: 4800,
      id: "b349a127-a9f0-40d8-b37c-f5b5166efa7b",
    } satisfies ClientImage

    const {getByTestId} = render(
      <MemoryRouter>
        <ThumbnailImage image={image} size="md" />
      </MemoryRouter>
    )
    const node = getByTestId("image-b349a127-a9f0-40d8-b37c-f5b5166efa7b")
    const img = within(node).getByRole("img")
    expect(img).toBeInTheDocument()
    // expect(img).
  })
})
