import supplyApi from "@/api/supply/supply.api";
import BaseComponent from "@/components/basic/base/BaseComponent";
import CustomText from "@/components/basic/custom/CustomText";
import { useResource } from "@/hooks/useResource";
import { useLocalSearchParams } from "expo-router";
import React, { useCallback } from "react";

//  Individual supply details
export default function SupplyDetailsScreen() {
  const { id } = useLocalSearchParams();
  const numericId = Number(id);

  const fetchSupply = useCallback(
    () => supplyApi.getSupply(numericId),
    [numericId],
  );

  const { data: supply, loading } = useResource<SupplyInformation>(fetchSupply);

  return (
    <>
      <BaseComponent isLoading={loading} title={supply?.name}>
        <CustomText text="Supply" />
      </BaseComponent>
    </>
  );
}
