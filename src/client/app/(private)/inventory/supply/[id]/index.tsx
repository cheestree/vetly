import supplyApi from "@/api/supply/supply.api";
import { SupplyInformation } from "@/api/supply/supply.output";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import SupplyDetailsContent from "@/components/inventory/SupplyDetailsContent";
import { useAuth } from "@/hooks/useAuth";
import { useResource } from "@/hooks/useResource";
import { useLocalSearchParams } from "expo-router";
import React, { useCallback, useMemo } from "react";

export default function SupplyDetailsScreen() {
  const { id } = useLocalSearchParams();
  const numericId = Number(id);

  const { information } = useAuth();
  const userClinicIds = useMemo(
    () => information?.clinics?.map((m) => m.clinic.id).filter(Boolean) ?? [],
    [information],
  );

  const fetchSupply = useCallback(async () => {
    for (const clinicId of userClinicIds) {
      try {
        const clinicSupplies = await supplyApi.getClinicSupplies(clinicId);
        const found = clinicSupplies.elements.find((s) => s.id === numericId);
        if (found) {
          return await supplyApi.getSupply(numericId);
        }
      } catch {}
    }
    return await supplyApi.getSupply(numericId);
  }, [numericId, userClinicIds]);

  const { data: supply, loading } = useResource<SupplyInformation>(fetchSupply);

  return (
    <BaseComponent isLoading={loading} title={supply?.name}>
      <PageHeader
        title={supply?.name || "Supply details"}
        description={""}
        buttons={[]}
      />
      <SupplyDetailsContent supply={supply} />
    </BaseComponent>
  );
}
