import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import { useAuth } from "@/hooks/useAuth";
import React from "react";

export default function InventoryScreen() {
  const { information } = useAuth();

  return (
    <>
      <BaseComponent title={"Inventory"}>
        <PageHeader
          title={"Inventory"}
          description={"Keep your clinics' stock updated and ready"}
          buttons={[
            {
              name: "Add New Item",
              icon: "plus",
              operation: () => {},
            },
          ]}
        />
      </BaseComponent>
    </>
  );
}
