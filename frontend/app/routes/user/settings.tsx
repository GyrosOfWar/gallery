import {Outlet} from "@remix-run/react"
import UserSettingsSidebar from "../../components/UserSettingsSidebar"

const UserSettingsLayout = () => {
  console.log("rendering user settings layout")
  return (
    <UserSettingsSidebar>
      <Outlet />
    </UserSettingsSidebar>
  )
}

export default UserSettingsLayout
