import type {LoaderFunction} from "@remix-run/node"
import {requireUser} from "~/services/auth.server"
import http from "~/util/http"

export const loader: LoaderFunction = async ({request, params}) => {
  const user = await requireUser(request)
  const {uuid, width} = params
  const queryParams = new URL(request.url).searchParams
  const extension = queryParams.get("extension")

  const response = await http.get(
    `/api/media/thumbnail/${width}/${uuid}?extension=${extension}`,
    user.accessToken,
    request.headers
  )
  return response
}
