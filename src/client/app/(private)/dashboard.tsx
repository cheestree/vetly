import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import { useAuth } from "@/hooks/useAuth";
import React from "react";

export default function DashboardScreen() {
  const { information } = useAuth();

  return (
    <BaseComponent title={"Dashboard"}>
      <PageHeader
        title={"Dashboard"}
        description={"Welcome back, " + information?.name}
      />
    </BaseComponent>
  );
}
