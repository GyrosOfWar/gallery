const {createRoutesFromFolders} = require("@remix-run/v1-route-convention")

/** @type {import('@remix-run/dev').AppConfig} */
module.exports = {
  future: {
    // makes the warning go away in v1.15+
    v2_routeConvention: true,
  },

  ignoredRouteFiles: ["**/.*"],
  serverDependenciesToBundle: [/^react-leaflet.*/, "@react-leaflet/core"],

  routes(defineRoutes) {
    // uses the v1 convention, works in v1.15+ and v2
    return createRoutesFromFolders(defineRoutes)
  },
}
