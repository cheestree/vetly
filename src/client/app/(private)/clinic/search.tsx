import clinicApi from "@/api/clinic/clinic.api";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import CustomFilterButton from "@/components/basic/custom/CustomFilterButton";
import ClinicFilterModal from "@/components/clinic/ClinicFilterModal";
import ClinicList from "@/components/clinic/list/CheckupList";
import React, { useState } from "react";

export default function ClinicSearchScreen() {
  const [modalVisible, setModalVisible] = useState(false);
  const [clinics, setClinics] = useState<
    RequestList<ClinicPreview> | undefined
  >(undefined);
  const [loading, setLoading] = useState(false);

  const handleSearch = async (params: ClinicQueryParams) => {
    setLoading(true);
    try {
      const data = await clinicApi.getClinics(params);
      setClinics(data);
      setModalVisible(false);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <BaseComponent isLoading={loading} title={"Clinics"}>
        <PageHeader
          title={"Clinics"}
          description={"Find the perfect clinic for your every need"}
          buttons={[]}
        />

        {clinics?.elements && <ClinicList clinics={clinics?.elements} />}

        <CustomFilterButton onPress={() => setModalVisible(true)} />

        <ClinicFilterModal
          visible={modalVisible}
          onDismiss={() => setModalVisible(false)}
          onSearch={async (params: ClinicQueryParams) => handleSearch(params)}
        />
      </BaseComponent>
    </>
  );
}
