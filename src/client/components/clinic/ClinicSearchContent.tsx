import clinicApi from "@/api/clinic/clinic.api";
import { ClinicQueryParams } from "@/api/clinic/clinic.input";
import { ClinicPreview } from "@/api/clinic/clinic.output";
import { RequestList } from "@/api/RequestList";
import React, { useState } from "react";
import PagingFooter from "../basic/base/PagingFooter";
import CustomFilterButton from "../basic/custom/CustomFilterButton";
import ClinicFilterModal from "./ClinicFilterModal";
import ClinicList from "./list/ClinicList";

export default function ClinicSearchContent() {
  const [modalVisible, setModalVisible] = useState(false);
  const [clinics, setClinics] = useState<RequestList<ClinicPreview>>();
  const [query, setQuery] = useState<ClinicQueryParams>({});
  const [page, setPage] = useState(0);

  const handleSearch = async (params: ClinicQueryParams, pageNum = 0) => {
    try {
      const data = await clinicApi.getClinics({ ...params, page: pageNum });
      setClinics(data);
      setQuery(params);
      setPage(pageNum);
      setModalVisible(false);
    } catch (err) {
      console.error(err);
    }
  };

  const handleNext = () => {
    if (clinics && page < clinics.totalPages - 1) {
      handleSearch(query, page + 1);
    }
  };

  const handlePrev = () => {
    if (clinics && page > 0) {
      handleSearch(query, page - 1);
    }
  };

  return (
    <>
      {clinics?.elements && <ClinicList clinics={clinics.elements} />}

      <PagingFooter
        onPrevious={handlePrev}
        onNext={handleNext}
        disablePrevious={!clinics || page <= 0}
        disableNext={!clinics || page >= clinics.totalPages - 1}
      />

      <CustomFilterButton onPress={() => setModalVisible(true)} />

      <ClinicFilterModal
        visible={modalVisible}
        onDismiss={() => setModalVisible(false)}
        onSearch={(params) => handleSearch(params, 0)}
      />
    </>
  );
}
