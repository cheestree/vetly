import guideApi from "@/api/guide/guide.api";
import { GuideQueryParams } from "@/api/guide/guide.input";
import { GuidePreview } from "@/api/guide/guide.output";
import { RequestList } from "@/api/RequestList";
import { useState } from "react";
import PagingFooter from "../basic/base/PagingFooter";
import CustomFilterButton from "../basic/custom/CustomFilterButton";
import OverlayContainer from "../basic/OverlayContainer";
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
      <GuideList guides={guides?.elements} />

      <PagingFooter
        onPrevious={handlePrev}
        onNext={handleNext}
        disablePrevious={!guides || page <= 0}
        disableNext={!guides || page >= guides.totalPages - 1}
      />

      <OverlayContainer>
        <CustomFilterButton onPress={() => setModalVisible(true)} />
      </OverlayContainer>

      <GuideFilterModal
        visible={modalVisible}
        onDismiss={() => setModalVisible(false)}
        onSearch={(params) => handleSearch(params, 0)}
      />
    </>
  );
}
