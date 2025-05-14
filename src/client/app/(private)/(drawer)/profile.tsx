import { View, Text } from "react-native";
import { useAuth } from "@/hooks/AuthContext";
import { usePageTitle } from "@/hooks/usePageTitle";

export default function Profile() {
  const { information } = useAuth();
  usePageTitle("Profile");

  return (
    <>
      <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
        <Text>Profile</Text>
        <Text>{information?.name}</Text>
      </View>
    </>
  );
}
