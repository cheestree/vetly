import { View, Text, Pressable, SafeAreaView } from "react-native";
import { useAuth } from "@/hooks/useAuth";
import { Stack, useNavigation, useRouter } from "expo-router";
import BaseComponent from "@/components/BaseComponent";

export default function SettingsScreen() {
  const router = useRouter();
  const { signOut } = useAuth();

  return (
    <>
      <BaseComponent isLoading={false} title={"Settings"}>
        <Text>Settings</Text>
        <Pressable
          onPress={() => {
            signOut();
            router.replace({ pathname: "/(public)" });
          }}
        >
          <Text>Sign Out</Text>
        </Pressable>
      </BaseComponent>
    </>
  );
}
