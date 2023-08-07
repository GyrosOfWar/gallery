import {Sidebar} from "flowbite-react"
import {HiUser, HiLockClosed} from "react-icons/hi"

const UserSettingsSidebar: React.FC<{
  children: React.ReactNode
}> = ({children}) => {
  return (
    <>
      <Sidebar aria-label="Usersettings sidebar">
        <Sidebar.Items>
          <Sidebar.ItemGroup>
            <Sidebar.Item to="settings/general/" icon={HiUser}>
              <p>General</p>
            </Sidebar.Item>
            <Sidebar.Item to="settings/security/" icon={HiLockClosed}>
              <p>Security</p>
            </Sidebar.Item>
          </Sidebar.ItemGroup>
        </Sidebar.Items>
      </Sidebar>
      <main
        className="flex flex-col container ml-auto mr-auto px-2 relative"
        style={{minHeight: "calc(100vh - 68px)"}}
      >
        {children}
      </main>
    </>
  )
}

export default UserSettingsSidebar
