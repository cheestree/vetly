import BaseComponent from "@/components/basic/BaseComponent";
import { Text, SafeAreaView } from "react-native";

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
