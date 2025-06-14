import BaseComponent from "@/components/basic/BaseComponent";
import CustomButton from "@/components/basic/CustomButton";
import PageHeader from "@/components/basic/PageHeader";
import { useAuth } from "@/hooks/useAuth";
import { useRouter } from "expo-router";

export default function SettingsScreen() {
  const router = useRouter();
  const { signOut } = useAuth();

  return (
    <>
      <BaseComponent title={"Settings"}>
        <PageHeader
          title={"Settings"}
          description={"Edit your profile and credentials"}
          buttons={[]}
        />
        <CustomButton
          onPress={() => {
            signOut();
            router.replace({ pathname: "/(public)" });
          }}
          text="Sign out"
        />
      </BaseComponent>
    </>
  );
}
