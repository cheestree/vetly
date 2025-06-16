import checkupApi from "@/api/checkup/checkup.api";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import { useResource } from "@/hooks/useResource";
import { useLocalSearchParams, useRouter } from "expo-router";
import { useCallback } from "react";

export default function CheckupEdit() {
  const { id } = useLocalSearchParams();
  const numericId = Number(id);
  const router = useRouter();

  const fetchCheckup = useCallback(
    () => checkupApi.getCheckup(numericId),
    [numericId],
  );

  const { data: checkup, loading } =
    useResource<CheckupInformation>(fetchCheckup);

  const handleSave = async (updatedCheckup: Partial<CheckupInformation>) => {
    try {
      await checkupApi.updateCheckup(numericId, updatedCheckup);
      router.back();
    } catch (error) {
      throw error;
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
    </BaseComponent>
  );
}
