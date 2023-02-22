import type {LoaderFunction} from "@remix-run/node"
import {requireUser} from "~/services/auth.server"
import {backendUrl} from "~/util/consts"

export const loader: LoaderFunction = async ({request, params}) => {
  const user = await requireUser(request)
  const {uuid, width, height} = params
  const queryParams = new URL(request.url).searchParams
  const extension = queryParams.get("extension")

  const response = await fetch(
    backendUrl(`/api/media/thumbnail/${width}/${height}/${uuid}?extension=${extension}`),
    {headers: {authorization: `Bearer ${user.accessToken}`}}
  )
  return response
}
