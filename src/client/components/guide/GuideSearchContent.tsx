import { CheckupQueryParams } from "@/api/checkup/checkup.input";
import guideApi from "@/api/guide/guide.api";
import { GuideQueryParams } from "@/api/guide/guide.input";
import { GuidePreview } from "@/api/guide/guide.output";
import { RequestList } from "@/api/RequestList";
import { useState } from "react";
import CustomFilterButton from "../basic/custom/CustomFilterButton";
import GuideFilterModal from "./GuideFilterModal";
import GuideList from "./list/GuideList";

export default function GuideSearchContent() {
  const [modalVisible, setModalVisible] = useState(false);
  const [guides, setGuides] = useState<RequestList<GuidePreview> | undefined>(
    undefined,
  );

  const handleSearch = async (
    params: CheckupQueryParams | GuideQueryParams,
  ) => {
    try {
      const data = await guideApi.getGuides(params);
      setGuides(data);
      setModalVisible(false);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <>
      {guides?.elements && <GuideList guides={guides.elements} />}

      <CustomFilterButton onPress={() => setModalVisible(true)} />

      <GuideFilterModal
        visible={modalVisible}
        onDismiss={() => setModalVisible(false)}
        onSearch={handleSearch}
      />
    </>
  );
}
