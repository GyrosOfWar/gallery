import {useLoaderData} from "@remix-run/react"
import type {LoaderFunction} from "react-router"
import type {User} from "~/services/auth.server"
import {requireUser} from "~/services/auth.server"

interface Data {
  user: User
}

export const loader: LoaderFunction = async ({request}) => {
  const user = await requireUser(request)
  return {user} satisfies Data
}

export default function Index() {
  const data = useLoaderData<Data>()
  return <div>Success, you are logged in as {data.user.username}</div>
}
