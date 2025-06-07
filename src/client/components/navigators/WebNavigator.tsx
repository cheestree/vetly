import { RouterProps } from "@/lib/types";
import Header from "./web/Header";
import { Stack } from "expo-router";

export function WebNavigator({ routes }: RouterProps) {
  return (
    <Stack
      screenOptions={{
        header: () => <Header routes={routes} />,
        headerShown: true,
      }}
    />
  );
}
