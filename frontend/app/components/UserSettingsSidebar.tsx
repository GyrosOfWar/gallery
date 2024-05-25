import {Sidebar} from "flowbite-react"
import {HiUser, HiLockClosed} from "react-icons/hi"

export function changeFlexSidebar() {
  //TODO: check if there is a nicer way to do this
  if (typeof window !== "undefined") {
    document.querySelector("#mainView")?.classList.add("usersettings-main")
  }
}

const UserSettingsSidebar: React.FC<{
  children: React.ReactNode
}> = ({children}) => {
  changeFlexSidebar()
  return (
    <>
      <Sidebar aria-label="Usersettings sidebar">
        <Sidebar.Items>
          <Sidebar.ItemGroup>
            <Sidebar.Item to="general/" icon={HiUser}>
              <p>General</p>
            </Sidebar.Item>
            <Sidebar.Item to="security/" icon={HiLockClosed}>
              <p>Security</p>
            </Sidebar.Item>
          </Sidebar.ItemGroup>
        </Sidebar.Items>
      </Sidebar>
      <main
        id="userSettingsSidebar"
        className="flex flex-col container ml-auto mr-auto px-2 relative"
        style={{minHeight: "calc(100vh - 68px)"}}
      >
        {children}
      </main>
    </>
  )
}

export default UserSettingsSidebar
