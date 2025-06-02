import CustomBottomTabBar from "@/components/tab/CustomBottomTabBar";
import { Tabs } from "expo-router";

export function MobileNavigator() {
  return (
    <Tabs
      screenOptions={{ headerShown: false }}
      tabBar={(props) => <CustomBottomTabBar {...props} />}
    />
  );
}
