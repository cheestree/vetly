import { Stack } from "expo-router";

export default function CheckupsLayout() {
  return (
    <Stack
      screenOptions={{
        headerShown: false,
        headerBackButtonDisplayMode: 'default',
      }}
    />
  );
}