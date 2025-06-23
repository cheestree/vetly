import checkupApi from "@/api/checkup/checkup.api";
import { CheckupQueryParams } from "@/api/checkup/checkup.input";
import { CheckupPreview } from "@/api/checkup/checkup.output";
import { RequestList } from "@/api/RequestList";
import { Role } from "@/api/user/user.output";
import { useAuth } from "@/hooks/useAuth";
import { hasRole } from "@/lib/utils";
import { useState } from "react";
import { View } from "react-native";
import CustomButton from "../basic/custom/CustomButton";
import CustomFilterButton from "../basic/custom/CustomFilterButton";
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
    } catch (err) {
      console.error(err);
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
      {checkups?.elements && <CheckupList checkups={checkups.elements} />}

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
          disabled={!checkups || page <= 0}
        />
        <CustomButton
          text="Next"
          onPress={handleNext}
          disabled={!checkups || page >= checkups.totalPages - 1}
        />
      </View>

      <CustomFilterButton onPress={() => setModalVisible(true)} />

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
