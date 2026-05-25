import guideApi from "@/api/guide/guide.api";
import { GuideUpdate } from "@/api/guide/guide.input";
import { GuideInformation } from "@/api/guide/guide.output";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import GuideEditContent from "@/components/guide/GuideEditContent";
import { useNumericRouteParam } from "@/hooks/useRouteParam";
import { useResource } from "@/hooks/useResource";
import * as DocumentPicker from "expo-document-picker";
import * as ImagePicker from "expo-image-picker";
import { router } from "expo-router";
import { useCallback } from "react";
import { Toast } from "toastify-react-native";

export default function GuideEditScreen() {
  const numericId = useNumericRouteParam("id");

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
    try {
      await guideApi.updateGuide(numericId, updatedGuide, file, image);
      router.back();
    } catch (e) {
      Toast.error("Failed to update guide.");
    }
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
