import { Stack, useLocalSearchParams } from "expo-router";
import React, { useEffect, useState } from "react";
import checkupApi from "@/api/checkup/checkup.api"
import BaseComponent from "@/components/basic/BaseComponent";
import CheckupDetailsContent from "@/components/checkup/CheckupDetailsContent";
import { useResource } from "@/hooks/useResource";

export default function CheckupDetails() {
  const { id } = useLocalSearchParams();
  const numericId = Number(id);

  const { data: checkup, loading } = useResource<CheckupInformation>(() => checkupApi.getCheckup(numericId))

  return (
    <>
      <Stack.Screen options={{ title: "Checkup " + checkup?.id }} />
      <BaseComponent isLoading={loading} title={"Checkup " + checkup?.id}>
        <CheckupDetailsContent checkup={checkup} />
      </BaseComponent>
    </>
  );
}
