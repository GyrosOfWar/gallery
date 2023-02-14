import {Authenticator} from "remix-auth"
import {sessionStorage} from "~/services/session.server"
import {FormStrategy} from "remix-auth-form"

export const BACKEND = "http://localhost:8080"

export function url(url: string): string {
  return `${BACKEND}${url}`
}

export interface Jwt {
  username: string
  access_token: string
  token_type: string
  expires_in: number
}

export const authenticator = new Authenticator<Jwt>(sessionStorage)

async function login(username: string, password: string): Promise<Jwt> {
  const response = await fetch(url("/login"), {
    method: "POST",
    body: new URLSearchParams([
      ["username", username],
      ["password", password],
    ]),
  })

  if (response.ok) {
    return await response.json()
  } else {
    const text = await response.text()
    throw new Error(
      "request failed with status code " +
        response.status +
        ", response: " +
        text
    )
  }
}

// Tell the Authenticator to use the form strategy
authenticator.use(
  new FormStrategy(async ({form}) => {
    const username = form.get("username") as string
    const password = form.get("password") as string
    return await login(username, password)
  }),
  "user-pass"
)
