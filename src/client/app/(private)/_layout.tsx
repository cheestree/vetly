import CustomDrawerContent from "@/components/drawer/CustomDrawerContent";
import Drawer from "expo-router/drawer";
import { useWindowDimensions } from "react-native";
import { GestureHandlerRootView } from "react-native-gesture-handler";

export default function PrivateLayout() {
  const dimensions = useWindowDimensions();
  const isDesktop = dimensions.width >= 768;

  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
      <Drawer
        drawerContent={(props) => <CustomDrawerContent {...props} />}
        screenOptions={() => ({
          drawerType: isDesktop ? "permanent" : "front",
          drawerStyle: {
            minWidth: isDesktop ? 250 : 0,
            width: isDesktop ? "15%" : "70%",
          },
          headerShown: !isDesktop,
        })}
      />
    </GestureHandlerRootView>
  );
}
