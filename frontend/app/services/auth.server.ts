import {Authenticator} from "remix-auth"
import {sessionStorage} from "~/services/session.server"
import {FormStrategy} from "remix-auth-form"
import * as jwt from "jsonwebtoken"
import {backendUrl} from "~/util/consts"

export type UserRole = "ADMIN" | "USER"

interface LoginResponse {
  username: string
  access_token: string
  token_type: string
  expires_in: number
}

export interface User {
  username: string
  roles: UserRole[]
  accessToken: string
  userId: number
  email: string
}

export const authenticator = new Authenticator<User>(sessionStorage)

async function login(username: string, password: string): Promise<User> {
  const loginUrl = backendUrl("/login")
  try {
    const response = await fetch(loginUrl, {
      method: "POST",
      body: new URLSearchParams([
        ["username", username],
        ["password", password],
      ]),
    })

    if (response.ok) {
      const data: LoginResponse = await response.json()
      const token = jwt.decode(data.access_token) as jwt.JwtPayload
      return {
        username: data.username,
        roles: token.roles,
        accessToken: data.access_token,
        userId: token.userId,
        email: token.email,
      }
    } else {
      const text = await response.text()
      throw new Error(
        `Request failed with status code ${response.status}, response: '${text}'`
      )
    }
  } catch (err) {
    console.error(err)
    return Promise.reject(err)
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

export async function requireUser(request: Request): Promise<User> {
  return await authenticator.isAuthenticated(request, {
    failureRedirect: "/auth/login",
  })
}
