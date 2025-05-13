import { View, Text } from "react-native";
import { usePageTitle } from "@/hooks/usePageTitle";

export default function Contact() {
  usePageTitle("Contact");

  return (
    <>
      <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
        <Text>Contact Page</Text>
      </View>
    </>
  );
}
