import { Text, Pressable } from "react-native";
import { useAuth } from "@/hooks/useAuth";
import { useRouter } from "expo-router";
import BaseComponent from "@/components/basic/BaseComponent";
import PageHeader from "@/components/basic/PageHeader";

export default function SettingsScreen() {
  const router = useRouter();
  const { signOut } = useAuth();

  return (
    <>
      <BaseComponent isLoading={false} title={"Settings"}>
        <PageHeader
          title={"Settings"}
          description={"Edit your profile and credentials"}
          buttons={[
          ]}
        />
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
