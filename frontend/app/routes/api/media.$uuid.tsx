import type {LoaderFunction} from "@remix-run/node"
import {requireUser} from "~/services/auth.server"
import http from "~/util/http"

export const loader: LoaderFunction = async ({request, params}) => {
  const user = await requireUser(request)
  const {uuid} = params
  const queryParams = new URL(request.url).searchParams
  const extension = queryParams.get("extension")
  const download = queryParams.get("download") === "true"
  if (!uuid || !extension) {
    return new Response("missing parameters", {status: 400})
  }

  const response = await http.get(
    `/api/media/${uuid}?extension=${extension}`,
    user.accessToken
  )
  if (download) {
    response.headers.append(
      "content-disposition",
      'attachment; filename="image.jpeg"'
    )
  }
  return response
}
