import type {ActionArgs} from "@remix-run/node"
import {authenticator, logoutUser} from "~/services/auth.server"

export async function action({request}: ActionArgs) {
  await logoutUser()
  await authenticator.logout(request, {redirectTo: "/"})
}
