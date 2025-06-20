import guideApi from "@/api/guide/guide.api";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import GuideCreateContent from "@/components/guide/GuideCreateContent";
import { router } from "expo-router";

export default function GuideCreateScreen() {
  const handleCreateGuide = async (createdGuide: GuideCreate) => {
    try {
      await guideApi.createGuide(createdGuide);
      router.back();
    } catch (error) {
      throw error;
    }
  };

  return (
    <BaseComponent title={"Create a guide"}>
      <PageHeader
        buttons={[]}
        title={"Create"}
        description={"Create a guide and update it later with documents"}
      />
      <GuideCreateContent onCreate={handleCreateGuide} />
    </BaseComponent>
  );
}
