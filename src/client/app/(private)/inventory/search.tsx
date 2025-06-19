import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import React from "react";

//  Supply search
export default function SupplySearchScreen() {
  return (
    <>
      <BaseComponent isLoading={false} title={"Search supplies"}>
        <PageHeader
          title={"Inventory"}
          description={"Search for supplies"}
          buttons={[]}
        />
      </BaseComponent>
    </>
  );
}
