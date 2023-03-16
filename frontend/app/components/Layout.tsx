import {Link, NavLink} from "@remix-run/react"
import {Navbar, Dropdown, useTheme} from "flowbite-react"
import type {User} from "~/services/auth.server"
import {
  MoonIcon,
  PhotoIcon,
  SunIcon,
  Cog6ToothIcon,
} from "@heroicons/react/24/outline"
import {HiCog, HiUserAdd, HiLogout} from "react-icons/hi"
import {useEffect} from "react"
import Avatar from "react-avatar"

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
    <Dropdown placement="bottom" label={label} inline >
      {children}
    </Dropdown>
  )
}

const DarkThemeToggle = () => {
  const {mode, toggleMode} = useTheme()

  const onToggleTheme = () => {
    if (toggleMode) {
      toggleMode()
      if (mode) {
        localStorage.setItem("theme", mode)
      }
    }
  }

  useEffect(() => {
    const localStorageTheme = localStorage.getItem("theme")
    if (localStorageTheme && localStorageTheme.length > 0) {
      // TODO
    }
  }, [])

  return (
    <button
      aria-label="Toggle dark mode"
      data-testid="dark-theme-toggle"
      onClick={onToggleTheme}
      type="button"
      className="px-2"
    >
      {mode === "dark" ? (
        <SunIcon aria-label="Currently dark mode" className="w-4 h-4" />
      ) : (
        <MoonIcon aria-label="Currently light mode" className="w-4 h-4" />
      )}
    </button>
  )
}

const Layout: React.FC<{children: React.ReactNode; user?: User}> = ({
  children,
  user,
}) => {
  return (
    <>
      <Navbar className="mb-4">
        <Navbar.Brand as={Link} to="/">
          <PhotoIcon className="w-8 h-8 mr-1" />
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
          <DarkThemeToggle />
          <DynamicDropdown
            label={<Cog6ToothIcon className="w-7 h-7" />}
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
          <DynamicDropdown
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
              <NavbarLink to="/user/settings" user={user} visibleFor="user">
                Settings
              </NavbarLink>
            </Dropdown.Item>
            <Dropdown.Item icon={HiLogout}>
              <NavbarLink to="/auth/logout" user={user} visibleFor="user">
                Logout
              </NavbarLink>
            </Dropdown.Item>
          </DynamicDropdown>
        </Navbar.Collapse>
      </Navbar>
      <main
        className="flex flex-col container ml-auto mr-auto px-2 relative"
        style={{minHeight: "calc(100vh - 68px)"}}
      >
        {children}
      </main>
    </>
  )
}

export default Layout
