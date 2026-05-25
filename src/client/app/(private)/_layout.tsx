import CustomDrawerContent from "@/components/drawer/CustomDrawerContent";
import PrivateHeader from "@/components/navigators/PrivateHeader";
import { checkRouteAccess, filterRoutesByAccess } from "@/lib/accessPolicy";
import { useAuthState } from "@/hooks/useAuth";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { drawerItems } from "@/lib/types";
import { Redirect, useSegments } from "expo-router";
import Drawer from "expo-router/drawer";
import { useCallback, useState } from "react";
import { ActivityIndicator, Platform, useWindowDimensions } from "react-native";

export default function PrivateLayout() {
  const { colours, styles } = useThemedStyles();
  const { width } = useWindowDimensions();
  const { user, information, loading, roles } = useAuthState();
  const segments = useSegments();
  const [isCollapsed, setIsCollapsed] = useState(true);
  const isDesktop = Platform.OS === "web" && width >= 768;

  const filteredRoutes = filterRoutesByAccess(
    drawerItems,
    user != null,
    information?.roles || [],
  );

  const HeaderComponent = useCallback(
    ({ navigation }) => (
      <PrivateHeader onToggleDrawer={() => navigation.toggleDrawer()} />
    ),
    [],
  );

  if (loading) {
    return (
      <ActivityIndicator
        animating
        color="#0000ff"
        size={64}
        style={styles.loader}
      />
    );
  }

  if (!information?.roles) {
    return <Redirect href="/login" />;
  }

  if (!checkRouteAccess(segments, roles)) {
    return <Redirect href="/dashboard" />;
  }

  const getDrawerWidth = () => {
    if (!isDesktop) {
      return undefined;
    }
    return isCollapsed ? 68 : 240;
  };

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
          width: getDrawerWidth(),
          backgroundColor: colours.secondaryBackground,
          borderRightColor: colours.border,
          borderRightWidth: 1,
        },
        headerStyle: {
          backgroundColor: colours.secondaryBackground,
          borderBottomColor: colours.border,
          borderBottomWidth: 1,
        },
        headerTintColor: colours.fontHeader,
        headerTitleStyle: {
          color: colours.fontHeader,
        },
        header: HeaderComponent,
      }}
    />
  );
}
