import guideApi from "@/api/guide/guide.api";
import { GuideUpdate } from "@/api/guide/guide.input";
import { GuideInformation } from "@/api/guide/guide.output";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import GuideEditContent from "@/components/guide/GuideEditContent";
import { useResource } from "@/hooks/useResource";
import * as DocumentPicker from "expo-document-picker";
import * as ImagePicker from "expo-image-picker";
import { router, useLocalSearchParams } from "expo-router";
import { useCallback } from "react";

export default function GuideEditScreen() {
  const { id } = useLocalSearchParams();
  const numericId = Number(id);

  const fetchGuide = useCallback(
    () => guideApi.getGuide(numericId),
    [numericId],
  );

  const { data: guide, loading } = useResource<GuideInformation>(fetchGuide);

  const handleSave = async (
    updatedGuide: GuideUpdate,
    file: DocumentPicker.DocumentPickerAsset | File | null,
    image: ImagePicker.ImagePickerAsset | File | null,
  ) => {
    await guideApi.updateGuide(numericId, updatedGuide, file, image);
    router.back();
  };

  return (
    <BaseComponent
      isLoading={loading && !guide}
      title={guide?.title + " - Edit" || "Guide Edit"}
    >
      <PageHeader title={"Edit"} description={guide?.title || "Guide"} />
      {guide && (
        <GuideEditContent guide={guide} onSave={handleSave} loading={loading} />
      )}
    </BaseComponent>
  );
}
