import {ActionFunctionArgs} from "@remix-run/node"
import {useSubmit} from "@remix-run/react"
import {useEffect, useRef} from "react"
import {authenticator} from "~/services/auth.server"

export async function action({request}: ActionFunctionArgs) {
  await authenticator.logout(request, {redirectTo: "/auth/login"})
}

const Logout = () => {
  const ref = useRef<HTMLFormElement>(null)
  const submit = useSubmit()
  useEffect(() => {
    submit(ref.current)
  }, [submit])

  return <form ref={ref} />
}

export default Logout
