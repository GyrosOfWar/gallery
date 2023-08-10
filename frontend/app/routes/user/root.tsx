import {Outlet} from "@remix-run/react"
import UserSettingsSidebar from "../../components/UserSettingsSidebar"

const UserSettingsPage = () => {
  return (
    <UserSettingsSidebar>
      <Outlet />
    </UserSettingsSidebar>
  )
}

export default UserSettingsPage
