import CustomDrawerContent from "@/components/drawer/CustomDrawerContent";
import { useNavigationState } from "@react-navigation/native";
import { useRouter } from "expo-router";
import { Drawer } from "expo-router/drawer";
import { useEffect } from "react";
import { BackHandler, useWindowDimensions } from "react-native";

export default function PrivateLayout() {
  const dimensions = useWindowDimensions();
  const isDesktop = dimensions.width >= 768;

  return (
    <Drawer
      drawerContent={(props) => <CustomDrawerContent {...props} />}
      screenOptions={{
        drawerType: isDesktop ? "permanent" : "front",
        headerShown: false,
        drawerStyle: {
          minWidth: isDesktop ? 250 : 0,
          width: isDesktop ? "15%" : "70%",
        },
      }}
    >
      {/* This wraps all screens inside the drawer in a common Stack */}
      <Drawer.Screen name="(drawer)" options={{ title: "Main" }} />
    </Drawer>
  );
}
