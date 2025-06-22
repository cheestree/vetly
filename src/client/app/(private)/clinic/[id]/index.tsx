import clinicApi from "@/api/clinic/clinic.api";
import { ClinicInformation } from "@/api/clinic/clinic.output";
import BaseComponent from "@/components/basic/base/BaseComponent";
import ClinicDetailsContent from "@/components/clinic/ClinicDetailsContent";
import { useResource } from "@/hooks/useResource";
import { Stack, useLocalSearchParams } from "expo-router";
import React, { useCallback } from "react";

export default function ClinicDetailsScreen() {
  const { id } = useLocalSearchParams();
  const numericId = Number(id);

  const fetchClinic = useCallback(
    () => clinicApi.getClinic(numericId),
    [numericId],
  );

  const { data: clinic, loading } = useResource<ClinicInformation>(fetchClinic);

  return (
    <>
      <Stack.Screen options={{ title: "Clinic " + clinic?.id }} />
      <BaseComponent isLoading={loading} title={"Clinic " + clinic?.id}>
        <ClinicDetailsContent clinic={clinic} />
      </BaseComponent>
    </>
  );
}
