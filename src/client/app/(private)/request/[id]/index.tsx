import requestApi from "@/api/request/request.api";
import { RequestStatus } from "@/api/request/request.input";
import { RequestInformation } from "@/api/request/request.output";
import { Role } from "@/api/user/user.output";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import RequestDetailsContent from "@/components/request/RequestDetailsContent";
import { useAuth } from "@/hooks/useAuth";
import { useResource } from "@/hooks/useResource";
import { router, useLocalSearchParams } from "expo-router";
import React, { useCallback } from "react";
import { Toast } from "toastify-react-native";

export default function RequestDetailsScreen() {
  const { id } = useLocalSearchParams();
  const stringId = String(id);
  const { information, hasRoles } = useAuth();

  const fetchRequest = useCallback(
    () => requestApi.getRequest(stringId),
    [stringId],
  );

  const { data: request, loading } =
    useResource<RequestInformation>(fetchRequest);

  const isAdmin = hasRoles(Role.ADMIN);
  const isUser = request?.user.publicId === information?.publicId;

  const handleDeleteRequest = async () => {
    try {
      await requestApi.deleteRequest(stringId);
      router.back();
    } catch (e) {
      Toast.error("Failed to delete request.");
    }
  };

  const handleRejectRequest = async () => {
    try {
      await requestApi.updateRequest(stringId, {
        decision: RequestStatus.REJECTED,
        justification: "Request rejected by admin.",
      });
      router.back();
    } catch (e) {
      Toast.error("Failed to reject request.");
    }
  };

  const handleAcceptRequest = async () => {
    try {
      await requestApi.updateRequest(stringId, {
        decision: RequestStatus.APPROVED,
        justification: "Request approved by admin.",
      });
      router.back();
    } catch (e) {
      Toast.error("Failed to accept request.");
    }
  };

  const adminButtons =
    request?.status === RequestStatus.PENDING
      ? isAdmin
        ? [
            {
              name: "Accept",
              icon: "check",
              operation: handleAcceptRequest,
            },
            {
              name: "Reject",
              icon: "times",
              operation: handleRejectRequest,
            },
            {
              name: "Delete",
              icon: "trash",
              operation: handleDeleteRequest,
            },
          ]
        : isUser
          ? [
              {
                name: "Delete",
                icon: "trash",
                operation: handleDeleteRequest,
              },
            ]
          : []
      : [];

  return (
    <BaseComponent isLoading={loading} title={"Request " + request?.id}>
      <PageHeader
        title={`Request #${request?.id ?? ""}`}
        description={request?.justification ?? ""}
        buttons={adminButtons}
      />
      <RequestDetailsContent request={request} />
    </BaseComponent>
  );
}
