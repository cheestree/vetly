import { Drawer } from "expo-router/drawer";
import { GestureHandlerRootView } from "react-native-gesture-handler";
import React from "react";
import CustomDrawerContent from "../drawer/CustomDrawerContent";
import { useWindowDimensions } from "react-native";

const Screens = [
  {
    name: "(private)/(drawer)/dashboard",
    options: { title: "Dashboard" },
  },
  {
    name: "(private)/(drawer)/settings",
    options: { title: "Settings" },
  },
  {
    name: "(private)/(drawer)/profile",
    options: { title: "Profile" },
  },
  {
    name: "(private)/(drawer)/checkups",
    options: { title: "Checkups" },
  },
  {
    name: "(private)/(drawer)/checkups/[checkupId]",
    options: { title: "Checkup" },
  },
  { name: "contact", options: { title: "Contact" } },
  { name: "about", options: { title: "About" } },
];

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
      >
        {Screens.map((screen) => (
          <Drawer.Screen
            key={screen.name}
            name={screen.name}
            options={screen.options}
          />
        ))}
      </Drawer>
    </GestureHandlerRootView>
  );
}
