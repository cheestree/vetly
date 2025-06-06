import BaseComponent from "@/components/BaseComponent";
import { Stack, Tabs } from "expo-router";
import { View, Text, SafeAreaView } from "react-native";

export default function Index() {
  return (
    <>
      <BaseComponent isLoading={false} title="Vetly">
        <SafeAreaView>
          <Text>Home Page</Text>
        </SafeAreaView>
      </BaseComponent>
    </>
  );
}
