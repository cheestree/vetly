import { Stack } from "expo-router";

export default function DrawerStackLayout() {
  return (
    <Stack
      screenOptions={{
        headerShown: false,
      }}
    />
  );
}
