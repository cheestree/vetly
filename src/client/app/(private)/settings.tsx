import { View, Text, Pressable } from "react-native";
import { useAuth } from "@/hooks/AuthContext";
import { usePageTitle } from "@/hooks/usePageTitle";

export default function Settings() {
  const { signOut } = useAuth();
  usePageTitle("Settings");

  return (
    <>
      <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
        <Text>Settings</Text>
        <Pressable onPress={() => signOut()}>
          <Text style={{ color: "blue", marginTop: 20 }}>Sign Out</Text>
        </Pressable>
      </View>
    </>
  );
}
