import supplyApi from "@/api/supply/supply.api";
import { SupplyUpdate } from "@/api/supply/supply.input";
import { SupplyInformation } from "@/api/supply/supply.output";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import SupplyDetailsContent from "@/components/inventory/SupplyDetailsContent";
import UpdateStockModal from "@/components/inventory/SupplyUpdateStockModal";
import { useAuth } from "@/hooks/useAuth";
import { useResource } from "@/hooks/useResource";
import { useLocalSearchParams } from "expo-router";
import React, { useCallback, useMemo, useState } from "react";

export default function SupplyDetailsScreen() {
  const { id } = useLocalSearchParams();
  const numericId = Number(id);

  const { information } = useAuth();
  const userClinicIds = useMemo(
    () => information?.clinics?.map((m) => m.clinic.id).filter(Boolean) ?? [],
    [information],
  );

  const [clinicSupply, setClinicSupply] = useState<any>(null);
  const [showStockModal, setShowStockModal] = useState(false);

  const fetchSupply = useCallback(async () => {
    for (const clinicId of userClinicIds) {
      try {
        const clinicSupplies = await supplyApi.getClinicSupplies(clinicId);
        const found = clinicSupplies.elements.find((s) => s.id === numericId);
        if (found) {
          setClinicSupply({ ...found, clinicId });
          return await supplyApi.getSupply(numericId);
        }
      } catch {}
    }
    setClinicSupply(null);
    return await supplyApi.getSupply(numericId);
  }, [numericId, userClinicIds]);

  const {
    data: supply,
    loading,
    refetch,
  } = useResource<SupplyInformation>(fetchSupply);

  const handleUpdateStock = async (newStock: SupplyUpdate) => {
    if (!clinicSupply) return;
    try {
      await supplyApi.updateSupply(clinicSupply.clinicId, numericId, newStock);
      setShowStockModal(false);
      refetch();
    } catch (err) {
      // handle error
    }
  };

  return (
    <BaseComponent isLoading={loading} title={supply?.name}>
      <PageHeader
        title={supply?.name || "Supply details"}
        description={""}
        buttons={
          clinicSupply
            ? [
                {
                  name: "Update Stock",
                  icon: "edit",
                  operation: () => setShowStockModal(true),
                },
              ]
            : []
        }
      />
      <SupplyDetailsContent supply={supply} clinicSupply={clinicSupply} />
      {/* Modal for updating stock */}
      {showStockModal && (
        <UpdateStockModal
          visible={showStockModal}
          currentStock={clinicSupply?.stock}
          onDismiss={() => setShowStockModal(false)}
          onSave={handleUpdateStock}
        />
      )}
    </BaseComponent>
  );
}
