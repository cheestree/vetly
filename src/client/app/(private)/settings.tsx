import { View, Text, Pressable } from "react-native";
import Title from "expo-router/head";
import { useAuth } from "@/hooks/AuthContext";

export default function Settings() {
  const { signOut } = useAuth();

  return (
    <>
      <Title>Settings</Title>
      <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
        <Text>Settings</Text>
        <Pressable onPress={() => signOut()}>
          <Text style={{ color: "blue", marginTop: 20 }}>Sign Out</Text>
        </Pressable>
      </View>
    </>
  );
}
