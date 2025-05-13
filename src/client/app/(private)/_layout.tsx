import CustomDrawerContent from "@/components/drawer/CustomDrawerContent";
import { Drawer } from "expo-router/drawer";
import { useWindowDimensions } from "react-native";

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
    ></Drawer>
  );
}
