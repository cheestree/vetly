import animalApi from "@/api/animal/animal.api";
import { AnimalQueryParams } from "@/api/animal/animal.input";
import { AnimalPreview } from "@/api/animal/animal.output";
import { RequestList } from "@/api/RequestList";
import { Role } from "@/api/user/user.output";
import { useAuth } from "@/hooks/useAuth";
import { hasRole } from "@/lib/utils";
import { useState } from "react";
import PagingFooter from "../basic/base/PagingFooter";
import CustomFilterButton from "../basic/custom/CustomFilterButton";
import OverlayContainer from "../basic/OverlayContainer";
import AnimalFilterModal from "./AnimalFilterModal";
import AnimalList from "./list/AnimalList";

export default function AnimalSearchContent() {
  const { information } = useAuth();
  const [modalVisible, setModalVisible] = useState(false);
  const [animals, setAnimals] = useState<RequestList<AnimalPreview>>();
  const [query, setQuery] = useState<AnimalQueryParams>({});
  const [page, setPage] = useState(0);

  const handleSearch = async (params: AnimalQueryParams, pageNum = 0) => {
    try {
      const data = await animalApi.getAllAnimals({ ...params, page: pageNum });
      setAnimals(data);
      setQuery(params);
      setPage(pageNum);
      setModalVisible(false);
    } catch (err) {
      console.error(err);
    }
  };

  const handleNext = () => {
    if (animals && page < animals.totalPages - 1) {
      handleSearch(query, page + 1);
    }
  };

  const handlePrev = () => {
    if (animals && page > 0) {
      handleSearch(query, page - 1);
    }
  };

  return (
    <>
      <AnimalList animals={animals?.elements} />

      <PagingFooter
        onPrevious={handlePrev}
        onNext={handleNext}
        disablePrevious={!animals || page <= 0}
        disableNext={!animals || page >= animals.totalPages - 1}
      />

      <OverlayContainer>
        <CustomFilterButton onPress={() => setModalVisible(true)} />
      </OverlayContainer>

      <AnimalFilterModal
        visible={modalVisible}
        onDismiss={() => setModalVisible(false)}
        onSearch={(params) => handleSearch(params, 0)}
        canSearchByUserEmail={hasRole(
          information.roles,
          Role.VETERINARIAN,
          Role.ADMIN,
        )}
      />
    </>
  );
}
