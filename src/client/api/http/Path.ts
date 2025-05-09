export const ApiPaths = {
    root: process.env.EXPO_PUBLIC_API_URL,
  users: {
    get: (id: string) => `/users/${id}`,
    get_user_profile: () => `/users/me`,
  },
  animals: {
    root: "/animals",

    get: (id: string) => `/animals/${id}`,
    create: "/animals",
    update: (id: string) => `/animals/${id}`,
    delete: (id: string) => `/animals/${id}`,

    get_user_animals: "/animals/self",
    get_all: "/animals",
  },
  requests: {
    root: "/requests",

    get: (id: string) => `/requests/${id}`,
    create: "/requests",
    update: (id: string) => `/requests/${id}`,
    delete: (id: string) => `/requests/${id}`,

    get_user_animals: "/requests/self",
    get_all: "/requests",
  },
  clinics: {
    root: "/clinics",

    get: (id: string) => `/clinics/${id}`,
    create: "/clinics",
    update: (id: string) => `/clinics/${id}`,
    delete: (id: string) => `/clinics/${id}`,

    get_all: "/clinics",
  },
  checkups: {
    root: "/checkups",

    get: (id: string) => `/checkups/${id}`,
    create: "/checkups",
    update: (id: string) => `/checkups/${id}`,
    delete: (id: string) => `/checkups/${id}`,

    get_all: "/checkups",
  },
  supplies: {
    root: "/supplies",

    get: (clinicId: string, supplyId: string) =>
      `/supplies/${clinicId}/supplies/${supplyId}`,
    create: "/supplies",
    update: (clinicId: string, supplyId: string) =>
      `/supplies/${clinicId}/supplies/${supplyId}`,
    delete: (clinicId: string, supplyId: string) =>
      `/supplies/${clinicId}/supplies/${supplyId}`,

    get_all: "/supplies",
    get_clinic_supplies: (clinicId: string) => `/supplies/${clinicId}/supplies`,
    get_supply: (supplyId: string) => `/supplies/${supplyId}`,
  },
  guides: {
    root: "/guides",

    get: (id: string) => `/guides/${id}`,
    create: "/guides",
    update: (id: string) => `/guides/${id}`,
    delete: (id: string) => `/guides/${id}`,

    get_all: "/guides",
  },
};
