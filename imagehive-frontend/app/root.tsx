import type {MetaFunction} from "@remix-run/node"
import {
  Links,
  LiveReload,
  Meta,
  Outlet,
  Scripts,
  ScrollRestoration,
} from "@remix-run/react"
import {Flowbite} from "flowbite-react"
import css from "./css/app.css"

export function links() {
  return [{rel: "stylesheet", href: css}]
}

export const meta: MetaFunction = () => ({
  charset: "utf-8",
  title: "Imagehive",
  viewport: "width=device-width,initial-scale=1",
})

const Layout: React.FC<{children: React.ReactNode}> = ({children}) => {
  return (
    <main className="flex flex-col container ml-auto mr-auto mt-4">
      {children}
    </main>
  )
}

export default function App() {
  return (
    <Flowbite>
      <html lang="en">
        <head>
          <Meta />
          <Links />
        </head>
        <body>
          <Layout>
            <Outlet />
          </Layout>
          <ScrollRestoration />
          <Scripts />
          <LiveReload />
        </body>
      </html>
    </Flowbite>
  )
}
