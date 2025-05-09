import { View, Text } from "react-native";
import Title from "expo-router/head";

export default function Profile() {
  return (
    <>
      <Title>Profile</Title>
      <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
        <Text>Profile</Text>
      </View>
    </>
  );
}
