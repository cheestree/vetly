import requestApi from "@/api/request/request.api";
import { RequestStatus } from "@/api/request/request.input";
import { RequestInformation } from "@/api/request/request.output";
import { Role } from "@/api/user/user.output";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import RequestDetailsContent from "@/components/request/RequestDetailsContent";
import { useAuth } from "@/hooks/useAuth";
import { useResource } from "@/hooks/useResource";
import { router, Stack, useLocalSearchParams } from "expo-router";
import React, { useCallback } from "react";

export default function RequestDetailsScreen() {
  const { id } = useLocalSearchParams();
  const stringId = String(id);

  const fetchRequest = useCallback(
    () => requestApi.getRequest(stringId),
    [stringId],
  );

  const { data: request, loading } =
    useResource<RequestInformation>(fetchRequest);

  const { information } = useAuth();
  const isAdmin = information?.roles?.includes(Role.ADMIN);

  const handleDeleteRequest = async () => {
    await requestApi.deleteRequest(stringId);
    router.back();
  };

  const handleRejectRequest = async () => {
    await requestApi.updateRequest(stringId, {
      decision: RequestStatus.REJECTED,
      justification: "Request rejected by admin.",
    });
    router.back();
  };

  const handleAcceptRequest = async () => {
    await requestApi.updateRequest(stringId, {
      decision: RequestStatus.APPROVED,
      justification: "Request approved by admin.",
    });
    router.back();
  };

  const adminButtons = isAdmin
    ? [
        {
          name: "Accept",
          icon: "check",
          operation: handleAcceptRequest,
        },
        {
          name: "Reject",
          icon: "close",
          operation: handleRejectRequest,
        },
        {
          name: "Delete",
          icon: "trash",
          operation: handleDeleteRequest,
        },
      ]
    : [];

  return (
    <>
      <Stack.Screen options={{ title: "Request " + request?.id }} />
      <BaseComponent isLoading={loading} title={"Request " + request?.id}>
        <PageHeader
          title={`Request #${request?.id ?? ""}`}
          description={request?.justification ?? ""}
          buttons={adminButtons}
        />
        <RequestDetailsContent request={request} />
      </BaseComponent>
    </>
  );
}
