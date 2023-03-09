import type {LoaderFunction} from "@remix-run/node"
import {requireUser} from "~/services/auth.server"
import http from "~/util/http"

export const loader: LoaderFunction = async ({request, params}) => {
  const user = await requireUser(request)
  const {uuid} = params
  if (!uuid) {
    return new Response("missing parameters", {status: 400})
  }

  //   const response = await http.get(
  //     `/api/media/${uuid}?extension=${extension}`,
  //     user.accessToken
  //   )
  //   return response
}
