import type {TypedResponse} from "@remix-run/node"
import {json} from "@remix-run/node"
import {useLoaderData} from "@remix-run/react"
import type {LoaderFunction} from "react-router"
import type {User} from "~/services/auth.server"
import {authenticator} from "~/services/auth.server"

interface Data {
  user: User
}

export const loader: LoaderFunction = async ({request}) => {
  const user = await authenticator.isAuthenticated(request, {
    failureRedirect: "/auth/login",
  })

  return json({
    user,
  }) satisfies TypedResponse<Data>
}

export default function Index() {
  const data = useLoaderData<Data>()

  return <div>Success, you are logged in as {data.user.username}</div>
}
