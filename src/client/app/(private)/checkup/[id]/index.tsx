import checkupApi from "@/api/checkup/checkup.api";
import BaseComponent from "@/components/basic/BaseComponent";
import CheckupDetailsContent from "@/components/checkup/CheckupDetailsContent";
import { useResource } from "@/hooks/useResource";
import ROUTES from "@/lib/routes";
import { router, Stack, useLocalSearchParams } from "expo-router";
import React, { useCallback } from "react";

export default function CheckupDetails() {
  const { id } = useLocalSearchParams();
  const numericId = Number(id);

  const fetchCheckup = useCallback(
    () => checkupApi.getCheckup(numericId),
    [numericId],
  );

  const { data: checkup, loading } =
    useResource<CheckupInformation>(fetchCheckup);

  return (
    <>
      <Stack.Screen options={{ title: "Checkup " + checkup?.id }} />
      <BaseComponent isLoading={loading} title={"Checkup " + checkup?.id}>
        <CheckupDetailsContent
          checkup={checkup}
          deleteCheckup={async () => {
            await checkupApi.deleteCheckup(numericId);
            router.back();
          }}
          editCheckup={() => {
            router.navigate({
              pathname: ROUTES.PRIVATE.CHECKUP.EDIT,
              params: { id: numericId },
            });
          }}
        />
      </BaseComponent>
    </>
  );
}
