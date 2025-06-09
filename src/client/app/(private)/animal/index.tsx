import BaseComponent from "@/components/basic/BaseComponent";
import PageHeader from "@/components/basic/PageHeader";
import React from "react";

export default function PetSearchScreen() {
  return (
    <>
      <BaseComponent isLoading={false} title={"Search Pets"}>
        <PageHeader
          title={"Pets"}
          description={"Manage patients' pets"}
          buttons={[
            {
              name: "New Pet",
              icon: 'plus',
              operation: () => {},
            },
          ]}
        />
      </BaseComponent>
    </>
  );
}
