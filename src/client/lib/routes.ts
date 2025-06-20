const ROUTES = {
  PRIVATE: {
    ANIMAL: {
      BASE: "/animal",
      SEARCH: "/animal/search",
      DETAILS: "/animal/[id]",
      EDIT: "/animal/[id]/edit",
      CREATE: "/animal/new",
    },
    CHECKUP: {
      BASE: "/checkup",
      SEARCH: "/checkup/search",
      DETAILS: "/checkup/[id]",
      EDIT: "/checkup/[id]/edit",
      CREATE: "/checkup/new",
    },
    CLINIC: {
      BASE: "/clinic",
      SEARCH: "/clinic/search",
      DETAILS: "/clinic/[id]",
      EDIT: "/clinic/[id]/edit",
      CREATE: "/clinic/new",
    },
    GUIDE: {
      BASE: "/guide",
      SEARCH: "/guide/search",
      DETAILS: "/guide/[id]",
      EDIT: "/guide/[id]/edit",
      CREATE: "/guide/new",
    },
    INVENTORY: {
      BASE: "/inventory",
      SEARCH: "/inventory/search",
      DETAILS: "/inventory/[id]",
    },
    REQUEST: {
      BASE: "/request",
      SEARCH: "/request/search",
      DETAILS: "/request/[id]",
      CREATE: "/request/new",
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
