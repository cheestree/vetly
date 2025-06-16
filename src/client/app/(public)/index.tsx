import BaseComponent from "@/components/basic/base/BaseComponent";
import { SafeAreaView, Text } from "react-native";

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
