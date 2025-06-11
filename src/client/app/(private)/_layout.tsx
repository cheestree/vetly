import CustomDrawerContent from "@/components/drawer/CustomDrawerContent";
import PrivateHeader from "@/components/navigators/PrivateHeader";
import { filterRoutesByAccess } from "@/handlers/Handlers";
import { useAuth } from "@/hooks/useAuth";
import { drawerItems } from "@/lib/types";
import Drawer from "expo-router/drawer";
import { useState } from "react";
import { Platform, useWindowDimensions } from "react-native";

export default function PrivateLayout() {
  const { width } = useWindowDimensions();
  const { user, information } = useAuth();
  const isDesktop = Platform.OS === "web" && width >= 768;

  const filteredRoutes = filterRoutesByAccess(
    drawerItems,
    user != null,
    information?.roles || [],
  );
  const [isCollapsed, setIsCollapsed] = useState(true);

  return (
    <Drawer
      backBehavior="history"
      drawerContent={(props) => (
        <CustomDrawerContent
          routes={filteredRoutes}
          isCollapsed={isCollapsed}
          toggleCollapse={() => setIsCollapsed(!isCollapsed)}
          {...props}
        />
      )}
      screenOptions={{
        drawerType: isDesktop ? "permanent" : "front",
        drawerStyle: {
          width: isCollapsed && isDesktop ? 68 : 240,
        },
        header: PrivateHeader
      }}
    />
  );
}
