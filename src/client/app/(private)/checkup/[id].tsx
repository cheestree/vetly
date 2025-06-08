import { Stack, useLocalSearchParams } from "expo-router";
import React, { useEffect, useState } from "react";
import CheckupServices from "@/api/services/CheckupServices";
import BaseComponent from "@/components/basic/BaseComponent";
import CheckupDetailsContent from "@/components/checkup/CheckupDetailsContent";
import { useResource } from "@/hooks/useResource";

export default function CheckupDetails() {
  const { id } = useLocalSearchParams();
  const { data: checkup, loading } = useResource<CheckupInformation>(() => CheckupServices.getCheckup(id[0]))

  return (
    <>
      <Stack.Screen options={{ title: "Checkup " + checkup?.id }} />
      <BaseComponent isLoading={loading} title={"Checkup " + checkup?.id}>
        <CheckupDetailsContent checkup={checkup} />
      </BaseComponent>
    </>
  );
}
