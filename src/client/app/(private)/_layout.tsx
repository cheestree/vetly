import CustomDrawerContent from "@/components/drawer/CustomDrawerContent";
import { FontAwesome } from "@expo/vector-icons";
import { useNavigationState } from "@react-navigation/native";
import { useRouter } from "expo-router";
import { Drawer } from "expo-router/drawer";
import { useEffect } from "react";
import { BackHandler, TouchableOpacity, useWindowDimensions } from "react-native";

export default function PrivateLayout() {
  const dimensions = useWindowDimensions();
  const isDesktop = dimensions.width >= 768;

  return (
    <Drawer
      drawerContent={(props) => <CustomDrawerContent {...props} />}
      screenOptions={({ navigation }) => ({
        drawerType: isDesktop ? 'permanent' : 'front',
        drawerStyle: {
          minWidth: isDesktop ? 250 : 0,
          width: isDesktop ? '15%' : '70%',
        },
        headerShown: !isDesktop, // Hide header for permanent drawer (desktop)
        headerLeft: () =>
          !isDesktop ? (
            <TouchableOpacity
              onPress={() => navigation.toggleDrawer()}
              style={{ paddingHorizontal: 16 }}
            >
              <FontAwesome name="bars" size={24} />
            </TouchableOpacity>
          ) : null
      })}
    >
      {/* This wraps all screens inside the drawer in a common Stack */}
      <Drawer.Screen name="(drawer)" />
    </Drawer>
  );
}
