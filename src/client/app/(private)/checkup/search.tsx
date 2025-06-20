import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import CheckupSearchContent from "@/components/checkup/CheckupSearchContent";
import ROUTES from "@/lib/routes";
import { router } from "expo-router";
import React from "react";

export default function CheckupSearchScreen() {
  return (
    <BaseComponent title="Search Checkups">
      <PageHeader
        title={"Checkups"}
        description={"Manage and schedule checkups for your patients"}
        buttons={[
          {
            name: "Create checkup",
            icon: "plus",
            operation: () => {
              router.navigate({
                pathname: ROUTES.PRIVATE.CHECKUP.CREATE,
              });
            },
          },
        ]}
      />
      <CheckupSearchContent />
    </BaseComponent>
  );
}
