import { Ionicons } from "@expo/vector-icons";
import ROUTES from "./routes";

export type Route = {
  name: string;
  route: string;
  label: string;
  authenticated: boolean;
  restrictedWhenAuthenticated?: boolean;
  roles: string[];
  icon: keyof typeof Ionicons.glyphMap;
};

export type RouterProps = {
  authenticated?: boolean;
  routes: Route[];
};

export type SectionProps = {
  roles: string[];
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
    name: "pets",
    label: "Pets",
    route: ROUTES.PRIVATE.ME.PETS,
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
    roles: ["VETERINARIAN", "ADMIN"],
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
];
