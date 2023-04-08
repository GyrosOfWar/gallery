import {LoaderFunction, json} from "@remix-run/node"
import type {useLoaderData} from "@remix-run/react"
import type {PageImageDTO} from "imagehive-client"
import {requireUser} from "~/services/auth.server"
import http from "~/util/http"

export interface Data {
  images: PageImageDTO
}

export type ClientImageList = ReturnType<
  typeof useLoaderData<Data>
>["images"]["content"]
export type ClientImage = ClientImageList[0]

export const loader: LoaderFunction = async ({request}) => {
  const user = await requireUser(request)
  const url = new URL(request.url)
  const query = url.searchParams
  if (!query.has("size")) {
    query.set("size", "20")
  }

  const queryString = query.get("query")
  if (typeof queryString === "string" && queryString.length === 0) {
    query.delete("query")
  }

  const requestUrl = `/api/images?${query.toString()}`
  const images: PageImageDTO = await http.getJson(requestUrl, user.accessToken)

  const data = {images} satisfies Data
  return json(data)
}
