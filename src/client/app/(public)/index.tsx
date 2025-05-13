import { usePageTitle } from "@/hooks/usePageTitle";
import { View, Text } from "react-native";

export default function Index() {
  usePageTitle("Home");

  return (
    <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
      <Text>Home Page</Text>
    </View>
  );
}
