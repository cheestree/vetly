import clinicApi from "@/api/clinic/clinic.api";
import { ClinicInformation } from "@/api/clinic/clinic.output";
import BaseComponent from "@/components/basic/base/BaseComponent";
import ClinicDetailsContent from "@/components/clinic/ClinicDetailsContent";
import { useNumericRouteParam } from "@/hooks/useRouteParam";
import { useResource } from "@/hooks/useResource";
import React, { useCallback } from "react";

export default function ClinicDetailsScreen() {
  const numericId = useNumericRouteParam("id");

  const fetchClinic = useCallback(
    () => clinicApi.getClinic(numericId),
    [numericId],
  );

  const { data: clinic, loading } = useResource<ClinicInformation>(fetchClinic);

  return (
    <BaseComponent isLoading={loading} title={"Clinic " + clinic?.id}>
      <ClinicDetailsContent clinic={clinic} />
    </BaseComponent>
  );
}
