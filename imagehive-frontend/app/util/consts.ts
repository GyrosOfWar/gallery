// TODO read from environment variable
const BACKEND = "http://localhost:8080"

export const backendUrl = (rest: string) => `${BACKEND}${rest}`
