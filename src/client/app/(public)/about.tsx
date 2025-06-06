import { View, Text, SafeAreaView } from "react-native";
import { Stack } from "expo-router";
import BaseComponent from "@/components/BaseComponent";

export default function AboutScreen() {
  return (
    <>
      <BaseComponent isLoading={false} title="About Vetly">
        <SafeAreaView>
          <Text>About Page</Text>
        </SafeAreaView>
      </BaseComponent>
    </>
  );
}
