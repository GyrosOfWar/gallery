import {Sidebar} from "flowbite-react"
import {HiUser, HiLockClosed} from "react-icons/hi"

const UserSettingsSidebar = () => {
  return (
    <Sidebar aria-label="Usersettings sidebar">
      <Sidebar.Items>
        <Sidebar.ItemGroup>
          <Sidebar.Item href="/general" icon={HiUser}>
            <p>General</p>
          </Sidebar.Item>
          <Sidebar.Item to="/security" icon={HiLockClosed}>
            <p>Security</p>
          </Sidebar.Item>
        </Sidebar.ItemGroup>
      </Sidebar.Items>
    </Sidebar>
  )
}

export default UserSettingsSidebar
