import { View, Text } from "react-native";
import { usePageTitle } from "@/hooks/usePageTitle";

export default function About() {
  usePageTitle("About");

  return (
    <>
      <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
        <Text>About Page</Text>
      </View>
    </>
  );
}
