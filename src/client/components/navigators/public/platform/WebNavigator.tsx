import Header from "../../../Header";
import { Stack } from "expo-router";

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
