import CustomBottomTabBar from "../tab/CustomBottomTabBar";
import Header from "../Header";
import { Stack, Tabs } from "expo-router";

export function MobileNavigator() {
  return (
    <Tabs
      screenOptions={{ headerShown: false }}
      tabBar={(props) => <CustomBottomTabBar {...props} />}
    />
  );
}

export function WebNavigator() {
  return (
    <Stack
      screenOptions={{
        header: () => Header(),
        headerShown: true,
      }}
    />
  );
}
