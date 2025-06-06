import BaseComponent from "@/components/BaseComponent";
import { Stack } from "expo-router";
import { View, Text, SafeAreaView } from "react-native";

export default function MyPetsScreen() {
  return (
    <>
      <BaseComponent isLoading={false} title={"Pets"}>
        <Text>Pets</Text>
      </BaseComponent>
    </>
  );
}
