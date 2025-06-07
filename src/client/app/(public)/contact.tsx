import BaseComponent from "@/components/basic/BaseComponent";
import { Text, SafeAreaView } from "react-native";

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
