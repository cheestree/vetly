const ROUTES = {
  PRIVATE: {
    ANIMAL: {
      SEARCH: "/animal",
      DETAILS: "/animal/[id]",
    },
    CHECKUP: {
      SEARCH: "/checkup",
      DETAILS: "/checkup/[id]",
    },
    CLINIC: {
      SEARCH: "/clinic",
      DETAILS: "/clinic/[id]",
    },
    ME: {
      DASHBOARD: "/me/dashboard",
      PETS: "/me/pets",
      SETTINGS: "/me/settings",
      PROFILE: "/me/profile",
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
