import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import ROUTES from "@/lib/routes";
import { router } from "expo-router";
import React from "react";

export default function CheckupScreen() {
  return (
    <>
      <BaseComponent title={"Checkups"}>
        <PageHeader
          title={"Checkups"}
          description={"Manage checkups"}
          buttons={[
            {
              name: "New Checkup",
              icon: "plus",
              operation: () => {
                router.navigate({
                  pathname: ROUTES.PRIVATE.CHECKUP.CREATE,
                });
              },
            },
            {
              name: "Search Checkups",
              icon: "search",
              operation: () => {
                router.navigate({
                  pathname: ROUTES.PRIVATE.CHECKUP.SEARCH,
                });
              },
            },
          ]}
        />
      </BaseComponent>
    </>
  );
}
