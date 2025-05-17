import { Drawer } from "expo-router/drawer";
import { GestureHandlerRootView } from "react-native-gesture-handler";
import React from "react";
import CustomDrawerContent from "../drawer/CustomDrawerContent";
import { useWindowDimensions } from "react-native";

export default function PrivateNavigator() {
  const dimensions = useWindowDimensions();
  const isDesktop = dimensions.width >= 768;

  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
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
      />
    </GestureHandlerRootView>
  );
}
