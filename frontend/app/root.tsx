import type {LoaderFunction} from "@remix-run/node"
import {json} from "@remix-run/node"
import {
  Links,
  Meta,
  Outlet,
  Scripts,
  ScrollRestoration,
  useLoaderData,
} from "@remix-run/react"
import {Flowbite, ThemeModeScript} from "flowbite-react"
import Layout from "./components/Layout"
import type {User} from "./services/auth.server"
import {authenticator} from "./services/auth.server"
import type {Device} from "./services/device.server"
import {detectDevice} from "./services/device.server"
import stylesheet from "~/tailwind.css?url"

export function links() {
  return [
    {rel: "stylesheet", href: stylesheet},
    {
      rel: "stylesheet",
      href: "https://unpkg.com/leaflet@1.9.3/dist/leaflet.css",
      integrity: "sha256-kLaT2GOSpHechhsozzB+flnD+zUyjE2LlfWPgU04xyI=",
      crossOrigin: "",
    },
  ]
}

export interface OutletData {
  user: User | null
  device: Device
}

export const loader: LoaderFunction = async ({request}) => {
  const user = await authenticator.isAuthenticated(request)
  const device = detectDevice(request.headers.get("user-agent"))
  return json({user, device} satisfies OutletData)
}

export default function App() {
  const data = useLoaderData<OutletData>()

  return (
    <html lang="en">
      <head>
        <meta charSet="utf-8" />
        <title>ImageHive</title>
        <meta name="viewport" content="width=device-width,initial-scale=1" />

        <Meta />
        <Links />
        <meta httpEquiv="Accept-CH" content="DPR, Viewport-Width, Width" />
      </head>
      <body className="bg-white dark:bg-gray-900 dark:text-white">
        <Flowbite>
          <Layout user={data.user!}>
            <Outlet context={data} />
          </Layout>
        </Flowbite>
        <ScrollRestoration />
        <Scripts />
        <ThemeModeScript />
      </body>
    </html>
  )
}
