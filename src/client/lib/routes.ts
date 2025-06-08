const ROUTES = {
  PRIVATE: {
    ANIMAL: {
      SEARCH: "/animal",
      DETAILS: "/animal/[id]",
    },
    CHECKUP: {
      BASE: "/checkup",
      SEARCH: "/checkup/search",
      DETAILS: "/checkup/[id]",
    },
    CLINIC: {
      BASE: "/clinic",
      SEARCH: "/clinic/search",
      DETAILS: "/clinic/[id]",
    },
    GUIDE: {
      BASE: "/guide",
      SEARCH: "/guide/search",
      DETAILS: "/guide/[id]",
    },
    INVENTORY: {
      BASE: "/inventory",
      SEARCH: "/inventory/search",
      DETAILS: "/inventory/[id]",
    },
    ME: {
      DASHBOARD: "/dashboard",
      SETTINGS: "/settings",
      PROFILE: "/profile",
    },
  },
  PUBLIC: {
    HOME: "/index",
    LOGIN: "/login",
    CONTACT: "/contact",
    ABOUT: "/about",
  },
} as const;

export default ROUTES;
