import {Link} from "@remix-run/react"
import {Navbar} from "flowbite-react"
import type {User} from "~/services/auth.server"
import {PhotoIcon} from "@heroicons/react/24/outline"

const Layout: React.FC<{children: React.ReactNode; user?: User}> = ({
  children,
  user,
}) => {
  const isLoggedIn = !!user?.username

  return (
    <>
      <Navbar className="mb-4">
        <Navbar.Brand as={Link} to="/">
          <PhotoIcon className="w-8 h-8 mr-1" />
          Imagehive
        </Navbar.Brand>
        <Navbar.Toggle />
        <Navbar.Collapse>
          {isLoggedIn && user.roles.includes("ADMIN") && (
            <Navbar.Link as={Link} to="/admin/user/create">
              Create user
            </Navbar.Link>
          )}
          <Navbar.Link
            to={isLoggedIn ? "/auth/logout" : "/auth/login"}
            as={Link}
          >
            {isLoggedIn ? "Logout" : "Login"}
          </Navbar.Link>
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
