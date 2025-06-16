import BaseComponent from "@/components/basic/base/BaseComponent";
import { SafeAreaView, Text } from "react-native";

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
