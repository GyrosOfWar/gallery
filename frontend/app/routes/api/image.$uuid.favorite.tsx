import type {ActionFunction} from "@remix-run/node"
import {requireUser} from "~/services/auth.server"
import http from "~/util/http"

export const action: ActionFunction = async ({request, params}) => {
  const user = await requireUser(request)
  const {uuid} = params
  if (!uuid) {
    return new Response("missing parameters", {status: 400})
  }

  const response = await http.postJson(
    `/api/images/${uuid}/favorite`,
    null,
    user.accessToken,
  )
  return response
}
