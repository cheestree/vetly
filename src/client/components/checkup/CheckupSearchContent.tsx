import checkupApi from "@/api/checkup/checkup.api";
import { CheckupQueryParams } from "@/api/checkup/checkup.input";
import { CheckupPreview } from "@/api/checkup/checkup.output";
import { RequestList } from "@/api/RequestList";
import { Role } from "@/api/user/user.output";
import { useAuth } from "@/hooks/useAuth";
import { hasRole } from "@/lib/utils";
import { useState } from "react";
import PagingFooter from "../basic/base/PagingFooter";
import CustomFilterButton from "../basic/custom/CustomFilterButton";
import OverlayContainer from "../basic/OverlayContainer";
import CheckupFilterModal from "./CheckupFilterModal";
import CheckupList from "./list/CheckupList";

export default function CheckupSearchContent() {
  const { information } = useAuth();
  const [modalVisible, setModalVisible] = useState(false);
  const [checkups, setCheckups] = useState<RequestList<CheckupPreview>>();
  const [query, setQuery] = useState<CheckupQueryParams>({});
  const [page, setPage] = useState(0);

  const handleSearch = async (params: CheckupQueryParams, pageNum = 0) => {
    try {
      const data = await checkupApi.getCheckups({ ...params, page: pageNum });
      setCheckups(data);
      setQuery(params);
      setPage(pageNum);
      setModalVisible(false);
    } catch (e) {
      console.error(e);
    }
  };

  const handleNext = () => {
    if (checkups && page < checkups.totalPages - 1) {
      handleSearch(query, page + 1);
    }
  };

  const handlePrev = () => {
    if (checkups && page > 0) {
      handleSearch(query, page - 1);
    }
  };

  return (
    <>
      <CheckupList checkups={checkups?.elements} />

      <PagingFooter
        onPrevious={handlePrev}
        onNext={handleNext}
        disablePrevious={!checkups || page <= 0}
        disableNext={!checkups || page >= checkups.totalPages - 1}
      />

      <OverlayContainer>
        <CustomFilterButton onPress={() => setModalVisible(true)} />
      </OverlayContainer>

      <CheckupFilterModal
        visible={modalVisible}
        onDismiss={() => setModalVisible(false)}
        onSearch={(params) => handleSearch(params, 0)}
        canSearchByUserId={hasRole(
          information.roles,
          Role.VETERINARIAN,
          Role.ADMIN,
        )}
      />
    </>
  );
}
