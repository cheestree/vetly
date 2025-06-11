import BaseComponent from "@/components/basic/BaseComponent";
import React, { useState } from "react";
import checkupApi from "@/api/checkup/checkup.api";
import PageHeader from "@/components/basic/PageHeader";
import CheckupList from "@/components/checkup/list/CheckupList";
import CheckupFilterModal from "@/components/checkup/CheckupFilterModal";
import FilterModalButton from "@/components/basic/FilterModelButton";

export default function CheckupSearchScreen() {
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

      <FilterModalButton onPress={() => setModalVisible(true)} />

      <CheckupFilterModal
        visible={modalVisible}
        onDismiss={() => setModalVisible(false)}
        onSearch={async (params: CheckupQueryParams) => handleSearch(params)}
      />
    </BaseComponent>
  );
}
