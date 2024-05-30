import {ActionFunctionArgs, LoaderFunctionArgs, json} from "@remix-run/node"
import {authenticator} from "~/services/auth.server"
import {Form} from "@remix-run/react"
import {Button, Checkbox, Label, TextInput} from "flowbite-react"

export async function action({request}: ActionFunctionArgs) {
  return await authenticator.authenticate("user-pass", request, {
    successRedirect: "/admin/create/user",
    failureRedirect: "/auth/login",
  })
}

export async function loader({request}: LoaderFunctionArgs) {
  const user = await authenticator.isAuthenticated(request, {
    failureRedirect: "/auth/login",
  })

  if (!user.roles.includes("ADMIN")) {
    return new Response(null, {status: 401})
  } else {
    return json(user)
  }
}

const CreateUser = () => {
  return (
    <Form
      className="flex flex-col gap-4 max-w-2xl w-full self-center"
      method="post"
    >
      <h1 className="text-3xl font-bold">Create User</h1>

      <div>
        <Label className="mb-2 block" htmlFor="username">
          Username
        </Label>
        <TextInput
          id="username"
          placeholder="Enter username..."
          type="text"
          name="username"
          required
        />
      </div>
      <div>
        <Label className="mb-2 block" htmlFor="generatePassword">
          Generate password
        </Label>
        <Checkbox id="generatePassword" name="generatePassword" />
      </div>
      <div>
        <Label className="mb-2 block" htmlFor="password">
          Password
        </Label>
        <TextInput
          id="password"
          placeholder="Enter password..."
          type="password"
          name="password"
          required
          autoComplete="current-password"
        />
      </div>
      <div>
        <Label className="mb-2 block" htmlFor="email">
          E-Mail
        </Label>
        <TextInput
          id="email"
          placeholder="Enter email..."
          type="text"
          name="email"
          required
        />
      </div>
      <div>
        <Label className="mb-2 block" htmlFor="admin">
          Admin
        </Label>
        <Checkbox id="admin" name="admin" />
      </div>
      <Button type="submit">Create</Button>
    </Form>
  )
}

export default CreateUser
