import checkupApi from "@/api/checkup/checkup.api";
import BaseComponent from "@/components/basic/BaseComponent";
import InformationContainer from "@/components/basic/InformationContainer";
import PageHeader from "@/components/basic/PageHeader";
import { useAuth } from "@/hooks/useAuth";
import React from "react";
import { Text } from "react-native";

export default function DashboardScreen() {
  const { information } = useAuth();

  return (
    <>
      <BaseComponent isLoading={false} title={"Dashboard"}>
        <PageHeader
          title={"Dashboard"}
          description={"Welcome back, " + information?.name}
          buttons={[]}
        />
        <InformationContainer
          loadItems={() => checkupApi.getTodayCheckups()}
          fallback={<Text>No checkups for today! Relax</Text>}
          label="checkups today"
          trend={+3}
        />
      </BaseComponent>
    </>
  );
}
