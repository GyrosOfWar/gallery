import {UAParser} from "ua-parser-js"

export type Device = "pc" | "phone" | "tablet"

export function detectDevice(header: string | null | undefined): Device {
  if (!header) {
    return "pc"
  }
  const parser = new UAParser(header)
  const device = parser.getDevice().type
  if (device === "console" || device === "smarttv") {
    return "pc"
  } else if (device === "tablet") {
    return "tablet"
  } else if (
    device === "mobile" ||
    device === "embedded" ||
    device === "wearable"
  ) {
    return "phone"
  } else {
    return "pc"
  }
}
