import { RequestList } from "@/api/RequestList";
import supplyApi from "@/api/supply/supply.api";
import { SupplyAssociate, SupplyQueryParams } from "@/api/supply/supply.input";
import { SupplyPreview } from "@/api/supply/supply.output";
import { useAuth } from "@/hooks/useAuth";
import { useState } from "react";
import PagingFooter from "../basic/base/PagingFooter";
import CustomButton from "../basic/custom/CustomButton";
import CustomFilterButton from "../basic/custom/CustomFilterButton";
import AddSupplyToClinicModal from "./AddSupplyToClinicModal"; // You will create this
import SupplyFilterModal from "./SupplyFilterModal";
import SupplyList from "./list/SupplyList";

export default function SupplySearchContent() {
  const { information } = useAuth();
  const clinics = information?.clinics ?? [];
  const hasClinics = clinics.length > 0;

  const [modalVisible, setModalVisible] = useState(false);
  const [addModalVisible, setAddModalVisible] = useState(false);
  const [supplies, setSupplies] = useState<RequestList<SupplyPreview>>();
  const [query, setQuery] = useState<SupplyQueryParams>({});
  const [page, setPage] = useState(0);

  const handleSearch = async (params: SupplyQueryParams, pageNum = 0) => {
    try {
      const data = await supplyApi.getSupplies({ ...params, page: pageNum });
      setSupplies(data);
      setQuery(params);
      setPage(pageNum);
      setModalVisible(false);
    } catch (err) {
      console.error(err);
    }
  };

  const handleNext = () => {
    if (supplies && page < supplies.totalPages - 1) {
      handleSearch(query, page + 1);
    }
  };

  const handlePrev = () => {
    if (supplies && page > 0) {
      handleSearch(query, page - 1);
    }
  };

  const handleAssociateSupply = async (
    clinicId: number,
    input: SupplyAssociate,
  ) => {
    try {
      await supplyApi.associateSupplyWithClinic(clinicId, input);
      setAddModalVisible(false);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <>
      {supplies?.elements && <SupplyList supplies={supplies.elements} />}

      <PagingFooter
        onPrevious={handlePrev}
        onNext={handleNext}
        disablePrevious={!supplies || page <= 0}
        disableNext={!supplies || page >= supplies.totalPages - 1}
      />

      <CustomFilterButton onPress={() => setModalVisible(true)} />

      {hasClinics && (
        <CustomButton
          text="Add Supply to Clinic"
          onPress={() => setAddModalVisible(true)}
        />
      )}

      <SupplyFilterModal
        visible={modalVisible}
        onDismiss={() => setModalVisible(false)}
        onSearch={(params) => handleSearch(params, 0)}
      />

      <AddSupplyToClinicModal
        visible={addModalVisible}
        onDismiss={() => setAddModalVisible(false)}
        clinics={clinics}
        supplies={supplies?.elements ?? []}
        onAssociate={handleAssociateSupply}
      />
    </>
  );
}
