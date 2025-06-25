import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import SupplySearchContent from "@/components/inventory/SupplySearchContent";
import React from "react";

export default function SupplySearchScreen() {
  return (
    <BaseComponent isLoading={false} title={"Search supplies"}>
      <PageHeader
        title={"Inventory"}
        description={"Search for supplies"}
        buttons={[]}
      />
      <SupplySearchContent />
    </BaseComponent>
  );
}
