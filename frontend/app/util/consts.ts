// TODO read from environment variable
const BACKEND = "http://localhost:8040"

export const backendUrl = (rest: string) => `${BACKEND}${rest}`

export const thumbnailUrl = (
  uuid: string,
  width: number,
  extension?: string,
) => {
  let url = `/api/thumbnail/${width}/${uuid}`
  if (extension) {
    url += `?extension=${extension}`
  }
  return url
}

export const originalImageUrl = (
  uuid: string,
  extension: string,
  download?: true,
) => {
  let url = `/api/media/${uuid}?extension=${extension}`
  if (download) {
    url += `&download=true`
  }
  return url
}
