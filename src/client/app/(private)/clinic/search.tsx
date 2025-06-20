import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import ClinicSearchContent from "@/components/clinic/ClinicSearchContent";
import React from "react";

export default function ClinicSearchScreen() {
  return (
    <BaseComponent title={"Clinics"}>
      <PageHeader
        title={"Clinics"}
        description={"Find the perfect clinic for your every need"}
        buttons={[]}
      />
      <ClinicSearchContent />
    </BaseComponent>
  );
}
