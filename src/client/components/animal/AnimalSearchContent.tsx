import { AnimalQueryParams } from "@/api/animal/animal.input";
import { AnimalPreview } from "@/api/animal/animal.output";
import { GuideQueryParams } from "@/api/guide/guide.input";
import { RequestList } from "@/api/RequestList";
import { Role } from "@/api/user/user.output";
import { useAuth } from "@/hooks/useAuth";
import { hasRole } from "@/lib/utils";
import { Dispatch, SetStateAction } from "react";
import PagingFooter from "../basic/base/PagingFooter";
import CustomFilterButton from "../basic/custom/CustomFilterButton";
import OverlayContainer from "../basic/OverlayContainer";
import AnimalFilterModal from "./AnimalFilterModal";
import AnimalList from "./list/AnimalList";

type AnimalSearchContentProps = {
  animals?: RequestList<AnimalPreview>;
  query: AnimalQueryParams;
  page: number;
  modalVisible: boolean;
  setModalVisible: Dispatch<SetStateAction<boolean>>;
  onSearch: (params: GuideQueryParams, pageNum?: number) => Promise<void>;
  onNext: () => void;
  onPrev: () => void;
};

export default function AnimalSearchContent({
  animals,
  query,
  page,
  modalVisible,
  setModalVisible,
  onSearch,
  onNext,
  onPrev,
}: AnimalSearchContentProps) {
  const { information } = useAuth();

  return (
    <>
      <AnimalList animals={animals?.elements} />

      <PagingFooter
        onPrevious={onPrev}
        onNext={onNext}
        disablePrevious={!animals || page <= 0}
        disableNext={!animals || page >= animals.totalPages - 1}
      />

      <OverlayContainer>
        <CustomFilterButton onPress={() => setModalVisible(true)} />
      </OverlayContainer>

      <AnimalFilterModal
        visible={modalVisible}
        onDismiss={() => setModalVisible(false)}
        onSearch={(params) => onSearch(params, 0)}
        canSearchByUserEmail={hasRole(
          information.roles,
          Role.VETERINARIAN,
          Role.ADMIN,
        )}
      />
    </>
  );
}
