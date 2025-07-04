import userApi from "@/api/user/user.api";
import { UserUpdate } from "@/api/user/user.input";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import CustomButton from "@/components/basic/custom/CustomButton";
import CustomTextInput from "@/components/basic/custom/CustomTextInput";
import { useAuth } from "@/hooks/useAuth";
import { useRouter } from "expo-router";
import { useState } from "react";
import { Alert, View } from "react-native";

export default function SettingsScreen() {
  const router = useRouter();
  const { signOut, information } = useAuth();
  const [formData, setFormData] = useState<UserUpdate>({
    username: information?.name,
    imageUrl: information?.imageUrl,
  });
  const [saving, setSaving] = useState(false);

  const handleUpdate = async () => {
    if (!formData.username.trim()) {
      Alert.alert("Validation Error", "Username cannot be empty.");
      return;
    }
    setSaving(true);
    try {
      await userApi.updateUserProfile(formData);
      Alert.alert("Success", "Username updated!");
    } catch {
      Alert.alert("Error", "Failed to update username.");
    }
    setSaving(false);
  };

  return (
    <BaseComponent title={"Settings"}>
      <PageHeader
        title={"Settings"}
        description={"Edit your profile and credentials"}
        buttons={[]}
      />
      <View style={{ gap: 16, marginBottom: 32 }}>
        <CustomTextInput
          textLabel="Username"
          value={formData.username}
          onChangeText={(text) =>
            setFormData((prev) => ({ ...prev, username: text }))
          }
          editable={!saving}
        />
        <CustomButton
          onPress={handleUpdate}
          text={saving ? "Saving..." : "Update Username"}
          disabled={saving}
        />
      </View>
      <CustomButton
        onPress={() => {
          signOut();
          router.replace({ pathname: "/(public)" });
        }}
        text="Sign out"
      />
    </BaseComponent>
  );
}
