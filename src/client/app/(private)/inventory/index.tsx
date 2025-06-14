import BaseComponent from "@/components/basic/BaseComponent";
import PageHeader from "@/components/basic/PageHeader";
import React from "react";

export default function InventoryScreen() {
  return (
    <>
      <BaseComponent title={"Inventory"}>
        <PageHeader
          title={"Inventory"}
          description={"Keep your clinics' stock updated and ready"}
          buttons={[]}
        />
      </BaseComponent>
    </>
  );
}
