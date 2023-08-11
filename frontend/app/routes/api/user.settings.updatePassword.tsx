import type {ActionFunction} from "@remix-run/node"
import {requireUser} from "~/services/auth.server"
import http from "~/util/http"

export const action: ActionFunction = async ({request}) => {
  const user = await requireUser(request)
  const body = await request.json()

  const response = await http.postJson(
    `/api/user/settings/updatePassword`,
    body,
    user.accessToken,
  )
  return response
}
