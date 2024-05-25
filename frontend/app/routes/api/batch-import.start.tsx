import type {LoaderFunction} from "@remix-run/node"
import {json} from "@remix-run/node"
import {requireUser} from "~/services/auth.server"
import http from "~/util/http"

export const loader: LoaderFunction = async ({request}) => {
  const {accessToken} = await requireUser(request)
  const data = await http.getJson("/api/batch-import/start", accessToken)

  return json(data)
}
