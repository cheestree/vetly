import { Role } from "@/api/user/user.output";
import { Ionicons } from "@expo/vector-icons";
import ROUTES from "./routes";

export type Route = {
  name: string;
  route: string;
  label: string;
  authenticated: boolean;
  restrictedWhenAuthenticated?: boolean;
  roles: Role[];
  icon: keyof typeof Ionicons.glyphMap;
};

export type RouterProps = {
  authenticated?: boolean;
  routes: Route[];
};

export type RangeProps = {
  startDate?: Date;
  endDate?: Date;
};

export const tabItems: Route[] = [
  {
    name: "index",
    route: "",
    label: "Home",
    authenticated: false,
    roles: [],
    icon: "home-outline",
  },
  {
    name: "about",
    route: ROUTES.PUBLIC.ABOUT,
    label: "About",
    authenticated: false,
    roles: [],
    icon: "information-circle-outline",
  },
  {
    name: "contact",
    label: "Contact",
    route: ROUTES.PUBLIC.CONTACT,
    authenticated: false,
    roles: [],
    icon: "call-outline",
  },
  {
    name: "login",
    label: "Login",
    route: ROUTES.PUBLIC.LOGIN,
    authenticated: false,
    restrictedWhenAuthenticated: true,
    roles: [],
    icon: "log-in-outline",
  },
  {
    name: "dashboard",
    label: "Dashboard",
    route: ROUTES.PRIVATE.ME.DASHBOARD,
    authenticated: true,
    roles: [],
    icon: "log-in-outline",
  },
];

export const drawerItems: Route[] = [
  {
    name: "dashboard",
    label: "Dashboard",
    route: ROUTES.PRIVATE.ME.DASHBOARD,
    authenticated: true,
    roles: [],
    icon: "home",
  },
  {
    name: "pet",
    label: "Pets",
    route: ROUTES.PRIVATE.ANIMAL.BASE,
    authenticated: true,
    roles: [],
    icon: "paw",
  },
  {
    name: "checkup",
    label: "Checkups",
    route: ROUTES.PRIVATE.CHECKUP.BASE,
    authenticated: true,
    roles: [],
    icon: "calendar",
  },
  {
    name: "inventory",
    label: "Inventory",
    route: ROUTES.PRIVATE.INVENTORY.BASE,
    authenticated: true,
    roles: [Role.VETERINARIAN, Role.ADMIN],
    icon: "warehouse",
  },
  {
    name: "clinics",
    label: "Clinics",
    route: ROUTES.PRIVATE.CLINIC.BASE,
    authenticated: true,
    roles: [],
    icon: "hospital",
  },
  {
    name: "guides",
    label: "Guides",
    route: ROUTES.PRIVATE.GUIDE.BASE,
    authenticated: false,
    roles: [],
    icon: "newspaper",
  },
  {
    name: "requests",
    label: "Requests",
    route: ROUTES.PRIVATE.REQUEST.BASE,
    authenticated: false,
    roles: [],
    icon: "clipboard",
  },
  {
    name: "settings",
    label: "Settings",
    route: ROUTES.PRIVATE.ME.SETTINGS,
    authenticated: true,
    roles: [],
    icon: "wrench",
  },
];

export const protectedRoutes: { path: string; roles: Role[] }[] = [
  {
    path: ROUTES.PRIVATE.ANIMAL.CREATE,
    roles: [Role.VETERINARIAN, Role.ADMIN],
  },
  { path: ROUTES.PRIVATE.ANIMAL.EDIT, roles: [Role.VETERINARIAN, Role.ADMIN] },

  {
    path: ROUTES.PRIVATE.CHECKUP.CREATE,
    roles: [Role.VETERINARIAN, Role.ADMIN],
  },
  { path: ROUTES.PRIVATE.CHECKUP.EDIT, roles: [Role.VETERINARIAN, Role.ADMIN] },

  // Clinic management (if only admins/vets can create/edit clinics)
  { path: ROUTES.PRIVATE.CLINIC.CREATE, roles: [Role.ADMIN] },
  { path: ROUTES.PRIVATE.CLINIC.EDIT, roles: [Role.ADMIN] },

  // Guide management
  { path: ROUTES.PRIVATE.GUIDE.CREATE, roles: [Role.VETERINARIAN, Role.ADMIN] },
  { path: ROUTES.PRIVATE.GUIDE.EDIT, roles: [Role.VETERINARIAN, Role.ADMIN] },

  // Request management (if only certain roles can create/edit)
  {
    path: ROUTES.PRIVATE.REQUEST.CREATE,
    roles: [Role.VETERINARIAN, Role.ADMIN],
  },
];
