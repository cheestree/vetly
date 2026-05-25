import checkupApi from "@/api/checkup/checkup.api";
import { CheckupInformation } from "@/api/checkup/checkup.output";
import { Role } from "@/api/user/user.output";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import CheckupDetailsContent from "@/components/checkup/CheckupDetailsContent";
import { useAuth } from "@/hooks/useAuth";
import { useNumericRouteParam } from "@/hooks/useRouteParam";
import { useResource } from "@/hooks/useResource";
import ROUTES, { routeWithId } from "@/lib/routes";
import { router } from "expo-router";
import React, { useCallback } from "react";
import { Toast } from "toastify-react-native";

export default function CheckupDetailsScreen() {
  const numericId = useNumericRouteParam("id");
  const { hasRoles } = useAuth();

  const fetchCheckup = useCallback(
    () => checkupApi.getCheckup(numericId),
    [numericId],
  );

  const { data: checkup, loading } =
    useResource<CheckupInformation>(fetchCheckup);

  const handleDeleteCheckup = async () => {
    try {
      await checkupApi.deleteCheckup(numericId);
      router.back();
    } catch (e) {
      Toast.error("Failed to delete checkup.");
    }
  };

  const handleEditCheckup = async () => {
    router.navigate(routeWithId(ROUTES.PRIVATE.CHECKUP.EDIT, numericId));
  };

  return (
    <BaseComponent isLoading={loading} title={"Checkup " + checkup?.id}>
      <PageHeader
        buttons={
          hasRoles(Role.ADMIN, Role.VETERINARIAN)
            ? [
                {
                  name: "Delete",
                  icon: "trash",
                  operation: handleDeleteCheckup,
                },
                {
                  name: "Edit",
                  icon: "pen",
                  operation: handleEditCheckup,
                },
              ]
            : []
        }
        title={"Details"}
        description={checkup?.title || "Checkup"}
      />
      <CheckupDetailsContent checkup={checkup} />
    </BaseComponent>
  );
}
