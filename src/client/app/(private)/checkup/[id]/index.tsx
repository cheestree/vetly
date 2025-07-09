import checkupApi from "@/api/checkup/checkup.api";
import { CheckupInformation } from "@/api/checkup/checkup.output";
import { Role } from "@/api/user/user.output";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import CheckupDetailsContent from "@/components/checkup/CheckupDetailsContent";
import { useAuth } from "@/hooks/useAuth";
import { useResource } from "@/hooks/useResource";
import ROUTES from "@/lib/routes";
import { router, useLocalSearchParams } from "expo-router";
import React, { useCallback } from "react";
import { Toast } from "toastify-react-native";

export default function CheckupDetailsScreen() {
  const { id } = useLocalSearchParams();
  const numericId = Number(id);
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
    router.navigate({
      pathname: ROUTES.PRIVATE.CHECKUP.EDIT,
      params: { id: numericId },
    });
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
