import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import GuideSearchContent from "@/components/guide/GuideSearchContent";
import { router } from "expo-router";
import React from "react";

export default function GuideSearchScreen() {
  return (
    <BaseComponent title={"Guides"}>
      <PageHeader
        title={"Guides"}
        description={
          "Read up on the latest curated guides for your furry friend"
        }
        buttons={[
          {
            name: "New Guide",
            icon: "plus",
            operation: () => {
              router.navigate({ pathname: "/guide/new" });
            },
          },
        ]}
      />
      <GuideSearchContent />
    </BaseComponent>
  );
}
