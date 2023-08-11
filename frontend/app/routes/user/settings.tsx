import {Outlet} from "@remix-run/react"
import UserSettingsSidebar from "../../components/UserSettingsSidebar"

const UserSettingsLayout = () => {
  return (
    <UserSettingsSidebar>
      <Outlet />
    </UserSettingsSidebar>
  )
}

export default UserSettingsLayout
