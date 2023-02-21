// TODO read from environment variable
const BACKEND = "http://localhost:8080"

export const backendUrl = (rest: string) => `${BACKEND}${rest}`

export const thumbnailUrl = (
  uuid: string,
  width: number,
  height: number,
  extension: string
) => {
  return `/api/thumbnail/${width}/${height}/${uuid}?extension=${extension}`
}
