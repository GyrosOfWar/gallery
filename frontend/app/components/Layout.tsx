import {Link, NavLink} from "@remix-run/react"
import {Navbar} from "flowbite-react"
import type {User} from "~/services/auth.server"
import {PhotoIcon} from "@heroicons/react/24/outline"

const navlinkStyle =
  "block py-2 pl-3 pr-4 text-gray-700 rounded hover:bg-gray-100 md:hover:bg-transparent md:border-0 md:hover:text-blue-700 md:p-0 dark:text-gray-400 md:dark:hover:text-white dark:hover:bg-gray-700 dark:hover:text-white md:dark:hover:bg-transparent"

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
          <NavbarLink to="/albums" user={user} visibleFor="user">
            Albums
          </NavbarLink>
          <NavbarLink to="/admin/user/create" user={user} visibleFor="admin">
            Create user
          </NavbarLink>

          {/* {isLoggedIn && user.roles.includes("ADMIN") && (
            <Navbar.Link as={Link} to="/admin/user/create">
              Create user
            </Navbar.Link>
          )}
          <Navbar.Link
            to={isLoggedIn ? "/auth/logout" : "/auth/login"}
            as={Link}
          >
            {isLoggedIn ? "Logout" : "Login"}
          </Navbar.Link> */}
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
