import guideApi from "@/api/guide/guide.api";
import { GuideCreate } from "@/api/guide/guide.input";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import GuideCreateContent from "@/components/guide/GuideCreateContent";
import ROUTES from "@/lib/routes";
import { DocumentPickerAsset } from "expo-document-picker";
import { ImagePickerAsset } from "expo-image-picker";
import { router } from "expo-router";

export default function GuideCreateScreen() {
  const handleCreateGuide = async (
    createdGuide: GuideCreate,
    file: DocumentPickerAsset | File | null,
    image: ImagePickerAsset | File | null,
  ) => {
    await guideApi.createGuide(createdGuide, file, image);
    router.replace(ROUTES.PRIVATE.GUIDE.SEARCH);
  };

  return (
    <BaseComponent title={"Create a guide"}>
      <PageHeader
        title={"Create"}
        description={"Create a guide and update it later with documents"}
      />
      <GuideCreateContent onCreate={handleCreateGuide} />
    </BaseComponent>
  );
}
