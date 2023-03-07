// TODO read from environment variable
const BACKEND = "http://localhost:8080"

export const backendUrl = (rest: string) => `${BACKEND}${rest}`

export const thumbnailUrl = (
  uuid: string,
  width: number,
  height: number,
  extension?: string
) => {
  let url = `/api/thumbnail/${width}/${height}/${uuid}`
  if (extension) {
    url += `?extension=${extension}`
  }
  return url
}

export const originalImageUrl = (uuid: string, extension: string) =>
  `/api/media/${uuid}?extension=${extension}`
