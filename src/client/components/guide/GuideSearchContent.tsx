import guideApi from "@/api/guide/guide.api";
import { GuideQueryParams } from "@/api/guide/guide.input";
import { GuidePreview } from "@/api/guide/guide.output";
import { RequestList } from "@/api/RequestList";
import { useState } from "react";
import { View } from "react-native";
import CustomButton from "../basic/custom/CustomButton";
import CustomFilterButton from "../basic/custom/CustomFilterButton";
import GuideFilterModal from "./GuideFilterModal";
import GuideList from "./list/GuideList";

export default function GuideSearchContent() {
  const [modalVisible, setModalVisible] = useState(false);
  const [guides, setGuides] = useState<RequestList<GuidePreview>>();
  const [query, setQuery] = useState<GuideQueryParams>({});
  const [page, setPage] = useState(0);

  const handleSearch = async (params: GuideQueryParams, pageNum = 0) => {
    try {
      const data = await guideApi.getGuides({ ...params, page: pageNum });
      setGuides(data);
      setQuery(params);
      setPage(pageNum);
      setModalVisible(false);
    } catch (err) {
      console.error(err);
    }
  };

  const handleNext = () => {
    if (guides && page < guides.totalPages - 1) {
      handleSearch(query, page + 1);
    }
  };

  const handlePrev = () => {
    if (guides && page > 0) {
      handleSearch(query, page - 1);
    }
  };

  return (
    <>
      {guides?.elements && <GuideList guides={guides.elements} />}

      <View
        style={{
          flexDirection: "row",
          justifyContent: "center",
          gap: 16,
          marginVertical: 8,
        }}
      >
        <CustomButton
          text="Previous"
          onPress={handlePrev}
          disabled={!guides || page <= 0}
        />
        <CustomButton
          text="Next"
          onPress={handleNext}
          disabled={!guides || page >= guides.totalPages - 1}
        />
      </View>

      <CustomFilterButton onPress={() => setModalVisible(true)} />

      <GuideFilterModal
        visible={modalVisible}
        onDismiss={() => setModalVisible(false)}
        onSearch={(params) => handleSearch(params, 0)}
      />
    </>
  );
}
