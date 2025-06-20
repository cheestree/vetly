import clinicApi from "@/api/clinic/clinic.api";
import { ClinicQueryParams } from "@/api/clinic/clinic.input";
import { ClinicPreview } from "@/api/clinic/clinic.output";
import { RequestList } from "@/api/RequestList";
import { useState } from "react";
import CustomFilterButton from "../basic/custom/CustomFilterButton";
import ClinicFilterModal from "./ClinicFilterModal";
import ClinicList from "./list/ClinicList";

export default function ClinicSearchContent() {
  const [modalVisible, setModalVisible] = useState(false);
  const [clinics, setClinics] = useState<RequestList<ClinicPreview>>();

  const handleSearch = async (params: ClinicQueryParams) => {
    try {
      const data = await clinicApi.getClinics(params);
      setClinics(data);
      setModalVisible(false);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <>
      {clinics?.elements && <ClinicList clinics={clinics.elements} />}

      <CustomFilterButton onPress={() => setModalVisible(true)} />

      <ClinicFilterModal
        visible={modalVisible}
        onDismiss={() => setModalVisible(false)}
        onSearch={handleSearch}
      />
    </>
  );
}
