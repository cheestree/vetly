import BaseComponent from "@/components/basic/BaseComponent";
import { useResource } from "@/hooks/useResource";
import { useLocalSearchParams, Stack } from "expo-router";
import React from "react";
import clinicApi from "@/api/clinic/clinic.api";
import ClinicDetailsContent from "@/components/clinic/ClinicDetailsContent";

export default function ClinicDetails() {
  const { id } = useLocalSearchParams();
  const numericId = Number(id);

  const { data: clinic, loading } = useResource<ClinicInformation>(() =>
    clinicApi.getClinic(numericId),
  );

  return (
    <>
      <Stack.Screen options={{ title: "Clinic " + clinic?.id }} />
      <BaseComponent isLoading={loading} title={"Clinic " + clinic?.id}>
        <ClinicDetailsContent clinic={clinic} />
      </BaseComponent>
    </>
  );
}
