import { Text, SafeAreaView } from "react-native";
import BaseComponent from "@/components/basic/BaseComponent";

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
