import {backendUrl} from "./consts"

function getUrl(path: string): string {
  const isClient = typeof document !== "undefined"
  if (isClient) {
    return path
  } else {
    return backendUrl(path)
  }
}

export type Method = "GET" | "POST" | "PUT" | "PATCH" | "DELETE"

class Http {
  async getJson<T>(path: string, accessToken?: string): Promise<T> {
    const response = await this.get(path, accessToken)
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return await response.json()
  }

  async get(path: string, accessToken?: string): Promise<Response> {
    return this.#request(path, "GET", undefined, accessToken)
  }

  async patchJson(
    path: string,
    payload: unknown,
    accessToken?: string
  ): Promise<Response> {
    return this.#request(path, "PATCH", payload, accessToken)
  }

  async postJson(
    path: string,
    payload: unknown,
    accessToken: string
  ): Promise<Response> {
    return this.#request(path, "POST", payload, accessToken)
  }

  async #request(
    path: string,
    method: Method,
    payload?: unknown,
    accessToken?: string
  ): Promise<Response> {
    const url = getUrl(path)
    try {
      const headers: HeadersInit = {}
      if (accessToken) {
        headers.authorization = `Bearer ${accessToken}}`
      }
      const requestInit: RequestInit = {headers, method}
      if (payload) {
        const json = JSON.stringify(payload)
        requestInit.body = json
        headers["content-type"] = "application/json"
      }
      const response = await fetch(url, requestInit)
      if (response.ok) {
        return response
      } else {
        const body = await response.text()
        throw new Error(
          `Request to ${method} ${url} failed: ${response.status}, response body: ${body}`
        )
      }
    } catch (e) {
      // eslint-disable-next-line @typescript-eslint/restrict-template-expressions
      console.error(`failed to ${method} ${url} with payload ${payload}`, e)
      throw e
    }
  }
}

const http = new Http()

export default http
