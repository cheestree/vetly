import BaseComponent from "@/components/basic/base/BaseComponent";
import { SafeAreaView, Text } from "react-native";

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
