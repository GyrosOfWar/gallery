import type {ActionArgs, LoaderArgs} from "@remix-run/node"
import {json} from "@remix-run/node"
import {Form} from "@remix-run/react"
import {Button, TextInput, Label} from "flowbite-react"
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
      className="flex flex-col gap-4 max-w-2xl w-full self-center"
      method="post"
    >
      <h1 className="text-3xl font-bold">Login</h1>

      <div>
        <Label className="mb-2 block" htmlFor="username">
          Username
        </Label>
        <TextInput
          id="username"
          placeholder="Enter your username..."
          type="text"
          name="username"
          required
        />
      </div>
      <div>
        <Label className="mb-2 block" htmlFor="password">
          Password
        </Label>
        <TextInput
          id="password"
          placeholder="Enter your password..."
          type="password"
          name="password"
          required
          autoComplete="current-password"
        />
      </div>
      <Button type="submit">Sign In</Button>
    </Form>
  )
}

export default Login
