import { View, Text } from "react-native";
import Title from "expo-router/head";

export default function Contact() {
  return (
    <>
      <Title>Contact</Title>
      <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
        <Text>Contact Page (Stack pushed)</Text>
      </View>
    </>
  );
}
