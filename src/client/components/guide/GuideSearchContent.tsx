import { GuideQueryParams } from "@/api/guide/guide.input";
import { GuidePreview } from "@/api/guide/guide.output";
import { RequestList } from "@/api/RequestList";
import { Dispatch, SetStateAction } from "react";
import PagingFooter from "../basic/base/PagingFooter";
import CustomFilterButton from "../basic/custom/CustomFilterButton";
import OverlayContainer from "../basic/OverlayContainer";
import GuideFilterModal from "./GuideFilterModal";
import GuideList from "./list/GuideList";

type GuideSearchContentProps = {
  guides?: RequestList<GuidePreview>;
  query: GuideQueryParams;
  page: number;
  modalVisible: boolean;
  setModalVisible: Dispatch<SetStateAction<boolean>>;
  onSearch: (params: GuideQueryParams, pageNum?: number) => Promise<void>;
  onNext: () => void;
  onPrev: () => void;
};

export function GuideSearchContent({
  guides,
  query,
  page,
  modalVisible,
  setModalVisible,
  onSearch,
  onNext,
  onPrev,
}: GuideSearchContentProps) {
  return (
    <>
      <GuideList guides={guides?.elements} />

      <PagingFooter
        onPrevious={onPrev}
        onNext={onNext}
        disablePrevious={!guides || page <= 0}
        disableNext={!guides || page >= (guides?.totalPages ?? 0) - 1}
      />

      <OverlayContainer>
        <CustomFilterButton onPress={() => setModalVisible(true)} />
      </OverlayContainer>

      <GuideFilterModal
        visible={modalVisible}
        onDismiss={() => setModalVisible(false)}
        onSearch={(params) => onSearch(params, 0)}
      />
    </>
  );
}
