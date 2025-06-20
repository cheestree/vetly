import checkupApi from "@/api/checkup/checkup.api";
import { CheckupQueryParams } from "@/api/checkup/checkup.input";
import { CheckupPreview } from "@/api/checkup/checkup.output";
import { RequestList } from "@/api/RequestList";
import { Role } from "@/api/user/user.output";
import { useAuth } from "@/hooks/useAuth";
import { hasRole } from "@/lib/utils";
import { useState } from "react";
import CustomFilterButton from "../basic/custom/CustomFilterButton";
import CheckupFilterModal from "./CheckupFilterModal";
import CheckupList from "./list/CheckupList";

export default function CheckupSearchContent() {
  const { information } = useAuth();
  const [modalVisible, setModalVisible] = useState(false);
  const [checkups, setCheckups] = useState<RequestList<CheckupPreview>>();

  const handleSearch = async (params: CheckupQueryParams) => {
    try {
      const data = await checkupApi.getCheckups(params);
      setCheckups(data);
      setModalVisible(false);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <>
      {checkups?.elements && <CheckupList checkups={checkups.elements} />}

      <CustomFilterButton onPress={() => setModalVisible(true)} />

      <CheckupFilterModal
        visible={modalVisible}
        onDismiss={() => setModalVisible(false)}
        onSearch={handleSearch}
        canSearchByUserId={hasRole(
          information.roles,
          Role.VETERINARIAN,
          Role.ADMIN,
        )}
      />
    </>
  );
}
