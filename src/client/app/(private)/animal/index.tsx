import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import ROUTES from "@/lib/routes";
import { router } from "expo-router";
import React from "react";

export default function AnimalScreen() {
  return (
    <>
      <BaseComponent title={"Pets"}>
        <PageHeader
          title={"Pets"}
          description={"Manage patients' pets"}
          buttons={[
            {
              name: "New Pet",
              icon: "plus",
              operation: () => {
                router.navigate({
                  pathname: ROUTES.PRIVATE.ANIMAL.CREATE,
                });
              },
            },
            {
              name: "Search Pets",
              icon: "search",
              operation: () => {
                router.navigate({
                  pathname: ROUTES.PRIVATE.ANIMAL.SEARCH,
                });
              },
            },
          ]}
        />
      </BaseComponent>
    </>
  );
}
