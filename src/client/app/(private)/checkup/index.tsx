import checkupApi from "@/api/checkup/checkup.api";
import BaseComponent from "@/components/basic/BaseComponent";
import CustomFilterButton from "@/components/basic/CustomFilterButton";
import PageHeader from "@/components/basic/PageHeader";
import CheckupFilterModal from "@/components/checkup/CheckupFilterModal";
import CheckupList from "@/components/checkup/list/CheckupList";
import { useAuth } from "@/hooks/useAuth";
import { hasRole } from "@/lib/utils";
import React, { useState } from "react";

export default function CheckupSearchScreen() {
  const { information } = useAuth();
  const [modalVisible, setModalVisible] = useState(false);
  const [checkups, setCheckups] = useState<
    RequestList<CheckupPreview> | undefined
  >(undefined);
  const [loading, setLoading] = useState(false);

  const handleSearch = async (params: CheckupQueryParams) => {
    setLoading(true);
    try {
      const data = await checkupApi.getCheckups(params);
      setCheckups(data);
      setModalVisible(false);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <BaseComponent isLoading={loading} title="Search Checkups">
      <PageHeader
        title={"Checkups"}
        description={"Manage and schedule checkups for your patients"}
        buttons={[
          {
            name: "Add Checkup",
            icon: "plus",
            operation: () => {},
          },
        ]}
      />

      {checkups?.elements && <CheckupList checkups={checkups?.elements} />}

      <CustomFilterButton onPress={() => setModalVisible(true)} />

      <CheckupFilterModal
        visible={modalVisible}
        onDismiss={() => setModalVisible(false)}
        onSearch={async (params: CheckupQueryParams) => handleSearch(params)}
        canSearchByUserId={hasRole(
          information?.roles || [],
          "VETERINARIAN",
          "ADMIN",
        )}
      />
    </BaseComponent>
  );
}
