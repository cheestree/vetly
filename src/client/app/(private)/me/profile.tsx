import { View, Text, SafeAreaView } from "react-native";
import { useAuth } from "@/hooks/useAuth";
import { Stack } from "expo-router";
import BaseComponent from "@/components/BaseComponent";

export default function ProfileScreen() {
  const { information } = useAuth();

  return (
    <>
      <BaseComponent isLoading={false} title={"Profile"}>
        <Text>Profile</Text>
        <Text>{information?.name}</Text>
      </BaseComponent>
    </>
  );
}
