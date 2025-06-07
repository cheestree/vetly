import { MobileNavigator } from "@/components/navigators/MobileNavigator";
import { WebNavigator } from "@/components/navigators/WebNavigator";
import { filterRoutesByAccess } from "@/handlers/Handlers";
import { useAuth } from "@/hooks/useAuth";
import { tabItems } from "@/lib/types";
import { useWindowDimensions, Platform } from "react-native";

export default function Layout() {
  const { width } = useWindowDimensions();
  const { user, information } = useAuth();
  const isDesktop = Platform.OS === "web" && width >= 768;
  const filteredRoutes = filterRoutesByAccess(
    tabItems,
    user != null,
    information?.roles || [],
  );

  return isDesktop ? (
    <WebNavigator routes={filteredRoutes} authenticated={user != null} />
  ) : (
    <MobileNavigator routes={filteredRoutes} authenticated={user != null} />
  );
}
