import { RouterProps } from "@/lib/types";
import Header from "../Header";
import { Stack } from "expo-router";

export function WebNavigator({ authenticated, routes }: RouterProps) {
  const filteredRoutes = routes.filter((tab) => {
    if (tab.route === "/login" && authenticated) return false;
    if (tab.route === "/me/dashboard" && !authenticated) return false;
    return true;
  });

  return (
    <Stack
      screenOptions={{
        header: () => <Header routes={filteredRoutes} />,
        headerShown: true,
      }}
    />
  );
}
