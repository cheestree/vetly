import checkupApi from "@/api/checkup/checkup.api";
import { CheckupUpdate } from "@/api/checkup/checkup.input";
import { CheckupInformation } from "@/api/checkup/checkup.output";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import CheckupEditContent from "@/components/checkup/CheckupEditContent";
import { useResource } from "@/hooks/useResource";
import { useLocalSearchParams, useRouter } from "expo-router";
import React, { useCallback } from "react";
import { Alert } from "react-native";

export default function CheckupEditScreen() {
  const { id } = useLocalSearchParams();
  const numericId = Number(id);
  const router = useRouter();

  const fetchCheckup = useCallback(
    () => checkupApi.getCheckup(numericId),
    [numericId],
  );

  const { data: checkup, loading } =
    useResource<CheckupInformation>(fetchCheckup);

  const handleSave = async (updatedCheckup: Partial<CheckupUpdate>) => {
    try {
      await checkupApi.updateCheckup(numericId, updatedCheckup);
      router.back();
    } catch (error) {
      Alert.alert("Error", "Failed to update checkup.");
    }
  };

  return (
    <BaseComponent
      isLoading={loading && !checkup}
      title={checkup?.title + " - Edit" || "Checkup Edit"}
    >
      <PageHeader
        buttons={[]}
        title={"Edit"}
        description={checkup?.title || "Checkup"}
      />
      {checkup && (
        <CheckupEditContent
          checkup={checkup}
          onSave={handleSave}
          loading={loading}
        />
      )}
    </BaseComponent>
  );
}
