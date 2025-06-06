import BaseComponent from "@/components/BaseComponent";
import { Stack, Tabs } from "expo-router";
import { View, Text, SafeAreaView } from "react-native";

export default function Contact() {
  return (
    <>
      <BaseComponent isLoading={false} title="Contacts">
        <SafeAreaView>
          <Text>Contact Page</Text>
        </SafeAreaView>
      </BaseComponent>
    </>
  );
}
