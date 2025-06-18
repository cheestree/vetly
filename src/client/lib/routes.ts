const ROUTES = {
  PRIVATE: {
    ANIMAL: {
      SEARCH: "/animal",
      DETAILS: "/animal/[id]",
      EDIT: "/animal/[id]/edit",
      CREATE: "/animal/new",
    },
    CHECKUP: {
      BASE: "/checkup",
      SEARCH: "/checkup/search",
      DETAILS: "/checkup/[id]",
      EDIT: "/checkup/[id]/edit",
    },
    CLINIC: {
      BASE: "/clinic",
      SEARCH: "/clinic/search",
      DETAILS: "/clinic/[id]",
      EDIT: "/clinic/[id]/edit",
    },
    GUIDE: {
      BASE: "/guide",
      SEARCH: "/guide/search",
      DETAILS: "/guide/[id]",
      EDIT: "/guide/[id]/edit",
    },
    INVENTORY: {
      BASE: "/inventory",
      SEARCH: "/inventory/search",
      DETAILS: "/inventory/[id]",
    },
    USER: {
      BASE: "/user",
      DETAILS: "/user/[id]",
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
