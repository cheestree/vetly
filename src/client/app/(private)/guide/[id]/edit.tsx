import guideApi from "@/api/guide/guide.api";
import { GuideUpdate } from "@/api/guide/guide.input";
import { GuideInformation } from "@/api/guide/guide.output";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import GuideEditContent from "@/components/guide/GuideEditContent";
import { useResource } from "@/hooks/useResource";
import { useLocalSearchParams, useRouter } from "expo-router";
import { useCallback } from "react";
import { Alert } from "react-native";

export default function GuideEditScreen() {
  const { id } = useLocalSearchParams();
  const numericId = Number(id);
  const router = useRouter();

  const fetchGuide = useCallback(
    () => guideApi.getGuide(numericId),
    [numericId],
  );

  const { data: guide, loading } = useResource<GuideInformation>(fetchGuide);

  const handleSave = async (updatedGuide: Partial<GuideUpdate>) => {
    try {
      await guideApi.updateGuide(numericId, updatedGuide);
      router.back();
    } catch (error) {
      Alert.alert("Error", "Failed to update guide.");
    }
  };

  return (
    <BaseComponent
      isLoading={loading && !guide}
      title={guide?.title + " - Edit" || "Guide Edit"}
    >
      <PageHeader
        buttons={[]}
        title={"Edit"}
        description={guide?.title || "Guide"}
      />
      {guide && (
        <GuideEditContent guide={guide} onSave={handleSave} loading={loading} />
      )}
    </BaseComponent>
  );
}
