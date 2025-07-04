import checkupApi from "@/api/checkup/checkup.api";
import { CheckupCreate } from "@/api/checkup/checkup.input";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import CheckupCreateContent from "@/components/checkup/CheckupCreateContent";
import { router } from "expo-router";
import { Alert } from "react-native";

export default function CheckupCreateScreen() {
  const handleCreateCheckup = async (createdCheckup: CheckupCreate) => {
    try {
      await checkupApi.createCheckup(createdCheckup);
      router.back();
    } catch (error) {
      Alert.alert("Error", "Failed to create checkup.");
    }
  };

  return (
    <BaseComponent title={"Create a checkup"}>
      <PageHeader
        buttons={[]}
        title={"Create"}
        description={"Create a checkup and update it later with documents"}
      />
      <CheckupCreateContent onCreate={handleCreateCheckup} />
    </BaseComponent>
  );
}
