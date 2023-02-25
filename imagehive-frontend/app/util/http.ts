import {backendUrl} from "./consts"

function getUrl(path: string): string {
  const isClient = typeof document !== "undefined"
  if (isClient) {
    return path
  } else {
    return backendUrl(path)
  }
}

const http = {
  async getJson<T>(path: string, accessToken: string): Promise<T> {
    const response = await this.get(path, accessToken)
    return await response.json()
  },

  async get(path: string, accessToken: string): Promise<Response> {
    const url = getUrl(path)
    try {
      const response = await fetch(url, {
        headers: {authorization: `Bearer ${accessToken}}`},
      })
      if (response.ok) {
        return response
      } else {
        const body = await response.text()
        throw new Error(
          `Request to get JSON from URL ${url} failed: ${response.status}, response body: ${body}`
        )
      }
    } catch (e) {
      throw e
    }
  },
}

export default http
