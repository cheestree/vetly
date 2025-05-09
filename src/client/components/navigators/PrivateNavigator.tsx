import { Drawer } from "expo-router/drawer";
import { GestureHandlerRootView } from "react-native-gesture-handler";
import React from "react";
import CustomDrawerContent from "../drawer/CustomDrawerContent";
import { useWindowDimensions } from "react-native";

const Screens = [
  {
    name: "(private)/dashboard",
    options: { title: "Dashboard" },
  },
  {
    name: "(private)/settings",
    options: { title: "Settings" },
  },
  {
    name: "(private)/profile",
    options: { title: "Profile" },
  },
  {
    name: "(private)/checkups",
    options: { title: "Checkups" },
  },
  {
    name: "(private)/checkups/[checkupId]",
    options: { title: "Checkup" }
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

/*
export default function PrivateNavigator() {
    return (
        <GestureHandlerRootView style={{ flex: 1 }}>
            <Drawer>
                <Drawer.Screen 
                    name="(private)/dashboard" 
                    options={{ title: 'Dashboard' }}
                />
                <Drawer.Screen 
                    name="(private)/settings" 
                    options={{ title: 'Settings' }}
                />
                <Drawer.Screen 
                    name="(private)/profile" 
                    options={{ title: 'Profile' }}
                />
                <Drawer.Screen 
                    name="(private)/checkups" 
                    options={{ title: 'Checkups' }}
                />
            </Drawer>
        </GestureHandlerRootView>
    )
    
}
*/
