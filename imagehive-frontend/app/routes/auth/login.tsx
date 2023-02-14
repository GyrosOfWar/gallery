import type {ActionArgs, LoaderArgs} from "@remix-run/node"
import {json} from "@remix-run/node"
import {Form} from "@remix-run/react"
import {Button} from "flowbite-react"
import {authenticator} from "~/services/auth.server"

export async function action({request}: ActionArgs) {
  return await authenticator.authenticate("user-pass", request, {
    successRedirect: "/",
    failureRedirect: "/auth/login",
  })
}

export async function loader({request}: LoaderArgs) {
  const response = await authenticator.isAuthenticated(request, {
    successRedirect: "/",
  })

  return json(response)
}

const Login = () => {
  return (
    <Form
      className="flex flex-col gap-2 max-w-2xl w-full self-center"
      method="post"
    >
      <h1 className="text-3xl font-bold">Login</h1>

      <input type="text" name="username" required />
      <input
        type="password"
        name="password"
        required
        autoComplete="current-password"
      />
      <Button color="grey" type="submit">
        Sign In
      </Button>
    </Form>
  )
}

export default Login
