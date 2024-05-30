import type {User} from "~/services/auth.server"
import {Link, NavLink} from "@remix-run/react"
import {Navbar, Dropdown} from "flowbite-react"
import {HiUserAdd, HiAdjustments} from "react-icons/hi"
import {HiPhoto} from "react-icons/hi2"

const navlinkStyle =
  "self-center block py-2 pl-3 pr-4 text-gray-700 rounded hover:bg-gray-100 md:hover:bg-transparent md:border-0 md:hover:text-blue-700 md:p-0 dark:text-gray-400 md:dark:hover:text-white dark:hover:bg-gray-700 dark:hover:text-white md:dark:hover:bg-transparent"

interface NavLinkProps {
  to: string
  children: React.ReactNode
  visibleFor: "everyone" | "user" | "admin"
  user?: User
}

const NavbarLink: React.FC<NavLinkProps> = ({
  to,
  children,
  visibleFor,
  user,
}) => {
  const isLoggedIn = !!user?.username
  const isAdmin = user?.roles.includes("ADMIN")
  let isVisible
  switch (visibleFor) {
    case "everyone":
      isVisible = true
      break
    case "admin":
      isVisible = isAdmin
      break
    case "user":
      isVisible = isLoggedIn
      break
  }

  if (!isVisible) {
    return null
  }

  return (
    <NavLink
      to={to}
      className={({isActive}) =>
        isActive ? `${navlinkStyle} underline` : navlinkStyle
      }
    >
      {children}
    </NavLink>
  )
}

interface DropdownProps {
  label: React.ReactNode
  children: React.ReactNode
  visibleFor: "everyone" | "user" | "admin"
  user?: User
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const DynamicDropdown: React.FC<DropdownProps> = ({
  label,
  children,
  visibleFor,
  user,
}) => {
  const isLoggedIn = !!user?.username
  const isAdmin = user?.roles.includes("ADMIN")
  let isVisible
  switch (visibleFor) {
    case "everyone":
      isVisible = true
      break
    case "admin":
      isVisible = isAdmin
      break
    case "user":
      isVisible = isLoggedIn
      break
  }

  if (!isVisible) {
    return null
  }

  return (
    <Dropdown placement="bottom" label={label} inline>
      {children}
    </Dropdown>
  )
}

const DarkThemeToggle = () => {
  // TODO useTheme was removed, so I guess we need to roll our own theming
  return null
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
function getInitials(name?: string) {
  if (!name) {
    return ""
  }
  const tokens = name.split(" ")
  if (tokens.length > 1) {
    return (tokens[0][0] + tokens[1][0]).toUpperCase()
  } else {
    return name.substring(0, 2).toUpperCase()
  }
}

const Layout: React.FC<{children: React.ReactNode; user?: User}> = ({
  children,
  user,
}) => {
  // const initials = getInitials(user?.username)

  return (
    <>
      <Navbar className="mb-4">
        <Navbar.Brand as={Link} to="/">
          <HiPhoto className="w-8 h-8 mr-1" />
          Imagehive
        </Navbar.Brand>
        <Navbar.Toggle />
        <Navbar.Collapse>
          <NavbarLink to="/" user={user} visibleFor="user">
            Images
          </NavbarLink>
          <NavbarLink to="/albums" user={user} visibleFor="user">
            Albums
          </NavbarLink>
          <NavbarLink to="/image/import" user={user} visibleFor="user">
            Import
          </NavbarLink>
          <DynamicDropdown
            label={<HiAdjustments className="w-8 h-8" />}
            user={user}
            visibleFor="admin"
          >
            <Dropdown.Item icon={HiUserAdd}>
              <NavbarLink
                to="/admin/user/create"
                user={user}
                visibleFor="admin"
              >
                Create User
              </NavbarLink>
            </Dropdown.Item>
          </DynamicDropdown>
          {/* <DynamicDropdown
            label={<Avatar name={user?.username} round={true} size="30px" />}
            user={user}
            visibleFor="user"
          >
            <Dropdown.Header>
              <span className="block text-sm">{user?.username}</span>
              <span className="block truncate text-sm font-medium">
                {user?.email}
              </span>
            </Dropdown.Header>
            <Dropdown.Item icon={HiCog}>
              <NavbarLink
                to="/user/settings/general"
                user={user}
                visibleFor="user"
              >
                Settings
              </NavbarLink>
            </Dropdown.Item>
            <Dropdown.Item icon={HiLogout}>
              <NavbarLink to="/auth/logout" user={user} visibleFor="user">
                Logout
              </NavbarLink>
            </Dropdown.Item>
          </DynamicDropdown> */}
          <DarkThemeToggle />
        </Navbar.Collapse>
      </Navbar>
      <main
        id="mainView"
        className="flex flex-col container ml-auto mr-auto px-2 relative"
        style={{minHeight: "calc(100vh - 68px)"}}
      >
        {children}
      </main>
    </>
  )
}

export default Layout
