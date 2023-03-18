import {expect, describe, it, vi, beforeAll, afterEach, afterAll} from "vitest"
import {render, within} from "@testing-library/react"
import ThumbnailImage, {getImageSize} from "../ThumbnailImage"
import type {ClientImage} from "~/routes"
import {MemoryRouter} from "react-router"
import {rest} from "msw"
import {setupServer} from "msw/node"
import userEvent from "@testing-library/user-event"

const image = {
  createdOn: new Date().toISOString(),
  extension: "jpeg",
  favorite: false,
  height: 3000,
  width: 4000,
  id: "b349a127-a9f0-40d8-b37c-f5b5166efa7b",
} satisfies ClientImage

const server = setupServer(
  rest.post("/api/image/*/favorite", (req, res, ctx) => {
    return res(ctx.json({...image, favorite: true}))
  })
)

beforeAll(() => server.listen())

afterEach(() => server.resetHandlers())

afterAll(() => server.close())

describe("ThumbnailImage", () => {
  it("should render an image with a link", () => {
    const {getByTestId} = render(
      <MemoryRouter>
        <ThumbnailImage image={image} size="md" />
      </MemoryRouter>
    )
    const node = getByTestId(`image-${image.id}`)
    const img = within(node).getByRole("img")
    expect(img).toBeInTheDocument()
    expect(img).toHaveAttribute("height")
    expect(img).toHaveAttribute("width")
    expect(img).toHaveAttribute("src")

    expect(node).toHaveAttribute("href")
  })

  it.skip("should send a request when the favorite button is clicked", async () => {
    const mockFn = vi.fn()
    const {getByTestId} = render(
      <MemoryRouter>
        <ThumbnailImage image={image} size="md" onImageFavorited={mockFn} />
      </MemoryRouter>
    )
    const button = getByTestId(`favorite-button-${image.id}`)
    expect(button).toBeInTheDocument()
    await userEvent.click(button)
  })
})

describe("getImageSize", () => {
  it("should correctly calculate the size of the image", () => {
    const [w, h] = getImageSize("xl", 4000, 3000)
    expect(w).toBeGreaterThan(h)
    expect(w / h).toBeCloseTo(4000 / 3000, 0.001)
  })
})
