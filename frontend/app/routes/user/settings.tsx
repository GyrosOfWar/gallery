import {Outlet} from "@remix-run/react"
import {Flowbite} from "flowbite-react"
import UserSettingsSidebar from "../../components/UserSettingsSidebar"

const UserSettingsPage = () => {
  return (
    <UserSettingsSidebar>
      <Outlet />
    </UserSettingsSidebar>
  )
}

export default UserSettingsPage
