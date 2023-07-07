import type {ActionFunction} from "@remix-run/node"
import {requireUser} from "~/services/auth.server"
import http from "~/util/http"

export const action: ActionFunction = async ({request, params}) => {
  const {accessToken} = await requireUser(request)
  const {uuid} = params
  return await http.postJson(
    `/api/batch-import/${uuid}/finish`,
    null,
    accessToken,
  )
}
