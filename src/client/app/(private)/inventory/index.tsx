import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import CustomText from "@/components/basic/custom/CustomText";
import { useAuth } from "@/hooks/useAuth";
import ROUTES from "@/lib/routes";
import { router } from "expo-router";
import React from "react";

export default function InventoryScreen() {
  const { information } = useAuth();

  const hasClinics = !!information?.clinics && information.clinics.length > 0;

  return (
    <>
      <BaseComponent title={"Inventory"}>
        <PageHeader
          title={"Inventory"}
          description={"Keep your clinics' stock updated and ready"}
          buttons={[
            ...(hasClinics
              ? [
                  {
                    name: "Add New Item",
                    icon: "plus",
                    operation: () => {},
                  },
                ]
              : []),
            {
              name: "Search supply",
              icon: "search",
              operation: () => {
                router.navigate({
                  pathname: ROUTES.PRIVATE.INVENTORY.SEARCH,
                });
              },
            },
          ]}
        />
        {hasClinics ? (
          <></>
        ) : (
          <CustomText text="You're not a member of a clinic yet." />
        )}
      </BaseComponent>
    </>
  );
}
