import userApi from "@/api/user/user.api";
import { UserUpdate } from "@/api/user/user.input";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import CustomButton from "@/components/basic/custom/CustomButton";
import CustomTextInput from "@/components/basic/custom/CustomTextInput";
import { useAuth } from "@/hooks/useAuth";
import { useForm } from "@/hooks/useForm";
import ROUTES from "@/lib/routes";
import { UserUpdateSchema } from "@/schemas/user.schema";
import { useRouter } from "expo-router";
import { View } from "react-native";
import { Toast } from "toastify-react-native";

type UserFormData = UserUpdate;

const initialUserFormData: UserFormData = {
  username: "",
};

export default function SettingsScreen() {
  const router = useRouter();
  const { signOut, information } = useAuth();
  const { form, handleInputChange } =
    useForm<UserFormData>(initialUserFormData);

  const handleUpdate = async () => {
    const parseResult = UserUpdateSchema.safeParse(form);

    if (!parseResult.success) {
      const firstError =
        parseResult.error.issues[0]?.message || "Validation error";
      Toast.error(firstError);
      return;
    }

    try {
      await userApi.updateUserProfile(parseResult.data);
      Toast.error("Username updated");
    } catch (e) {
      Toast.error("Failed to update username.");
    }
  };

  return (
    <BaseComponent title={"Settings"}>
      <PageHeader
        title={"Settings"}
        description={"Edit your profile and credentials"}
      />
      <View style={{ gap: 16, marginBottom: 32 }}>
        <CustomTextInput
          textLabel="Username"
          value={form.username}
          onChangeText={(text) => handleInputChange("username", text)}
        />
        <CustomButton onPress={handleUpdate} />
      </View>
      <CustomButton
        onPress={() => {
          signOut();
          router.replace({ pathname: ROUTES.PUBLIC.HOME });
        }}
        text="Sign out"
      />
    </BaseComponent>
  );
}
