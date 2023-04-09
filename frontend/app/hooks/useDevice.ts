import {useOutletContext} from "@remix-run/react"
import type {OutletData} from "~/root"
import type {Device} from "~/services/device.server"

export default function useDevice(): Device {
  const outletContext = useOutletContext<OutletData>()
  return outletContext.device
}
