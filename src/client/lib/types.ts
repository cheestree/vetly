import { Ionicons } from "@expo/vector-icons";
import ROUTES from "./routes";

export type Route = {
  name: string;
  route: string;
  tabBarLabel: string;
  icon: keyof typeof Ionicons.glyphMap;
};

export type RouterProps = {
  authenticated?: boolean;
  routes: Route[];
};

export const tabs: Route[] = [
  { name: "index", tabBarLabel: "Home", route: "", icon: "home-outline" },
  {
    name: "about",
    tabBarLabel: "About",
    route: ROUTES.PUBLIC.ABOUT,
    icon: "information-circle-outline",
  },
  {
    name: "contact",
    tabBarLabel: "Contact",
    route: ROUTES.PUBLIC.CONTACT,
    icon: "call-outline",
  },
  {
    name: "login",
    tabBarLabel: "Login",
    route: ROUTES.PUBLIC.LOGIN,
    icon: "log-in-outline",
  },
  {
    name: "dashboard",
    tabBarLabel: "Dashboard",
    route: ROUTES.PRIVATE.ME.DASHBOARD,
    icon: "log-in-outline",
  },
];
