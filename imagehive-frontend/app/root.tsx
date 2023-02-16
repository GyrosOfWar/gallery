import type {LoaderFunction, MetaFunction} from "@remix-run/node"
import {json} from "@remix-run/node"
import {
  Links,
  LiveReload,
  Meta,
  Outlet,
  Scripts,
  ScrollRestoration,
  useLoaderData,
} from "@remix-run/react"
import {Flowbite} from "flowbite-react"
import Layout from "./components/Layout"
import css from "./css/app.css"
import type {User} from "./services/auth.server"
import {authenticator} from "./services/auth.server"

export function links() {
  return [{rel: "stylesheet", href: css}]
}

export const meta: MetaFunction = () => ({
  charset: "utf-8",
  title: "Imagehive",
  viewport: "width=device-width,initial-scale=1",
})

export const loader: LoaderFunction = async ({request}) => {
  const user = await authenticator.isAuthenticated(request)
  return json(user)
}

export default function App() {
  const user = useLoaderData<User>()

  return (
    <Flowbite>
      <html lang="en">
        <head>
          <Meta />
          <Links />
        </head>
        <body>
          <Layout user={user}>
            <Outlet context={{user}} />
          </Layout>
          <ScrollRestoration />
          <Scripts />
          <LiveReload />
        </body>
      </html>
    </Flowbite>
  )
}
