import guideApi from "@/api/guide/guide.api";
import { GuideInformation } from "@/api/guide/guide.output";
import { Role } from "@/api/user/user.output";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import GuideDetailsContent from "@/components/guide/GuideDetailsContent";
import { useAuth } from "@/hooks/useAuth";
import { useResource } from "@/hooks/useResource";
import ROUTES from "@/lib/routes";
import { router, Stack, useLocalSearchParams } from "expo-router";
import React, { useCallback } from "react";

export default function GuideDetailsScreen() {
  const { id } = useLocalSearchParams();
  const numericId = Number(id);
  const { information, hasRoles } = useAuth();

  const fetchGuide = useCallback(
    () => guideApi.getGuide(numericId),
    [numericId],
  );

  const { data: guide, loading } = useResource<GuideInformation>(fetchGuide);

  const isAdmin = hasRoles(Role.ADMIN);
  const isAuthor = guide?.author.id !== information?.publicId;

  const handleDeleteGuide = async () => {
    await guideApi.deleteGuide(numericId);
    router.back();
  };

  const handleEditGuide = () => {
    router.push({
      pathname: ROUTES.PRIVATE.GUIDE.EDIT,
      params: { id: numericId },
    });
  };

  const buttons =
    isAdmin || isAuthor
      ? [
          {
            name: "Delete",
            icon: "trash",
            operation: handleDeleteGuide,
          },
          {
            name: "Edit",
            icon: "pen",
            operation: handleEditGuide,
          },
        ]
      : [];

  return (
    <>
      <Stack.Screen options={{ title: guide?.title }} />
      <BaseComponent isLoading={loading} title={guide?.title}>
        <PageHeader
          buttons={buttons}
          title={guide?.title || "No title provided"}
          description={guide?.description || "No description provided"}
        />
        <GuideDetailsContent guide={guide} />
      </BaseComponent>
    </>
  );
}
