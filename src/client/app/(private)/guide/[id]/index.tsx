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
import { Alert } from "react-native";

export default function GuideDetailsScreen() {
  const { id } = useLocalSearchParams();
  const numericId = Number(id);
  const { information, hasRoles } = useAuth();

  const fetchGuide = useCallback(
    () => guideApi.getGuide(numericId),
    [numericId],
  );

  const { data: guide, loading } = useResource<GuideInformation>(fetchGuide);
  const isAuthor =
    information?.publicId &&
    guide.author.id !== information.publicId &&
    hasRoles(Role.ADMIN, Role.VETERINARIAN);

  const handleDeleteGuide = async () => {
    try {
      await guideApi.deleteGuide(numericId);
      router.back();
    } catch (error) {
      Alert.alert("Error", "Failed to delete guide.");
    }
  };

  const handleEditGuide = () => {
    router.push({
      pathname: ROUTES.PRIVATE.GUIDE.EDIT,
      params: { id: numericId },
    });
  };

  return (
    <>
      <Stack.Screen options={{ title: guide?.title }} />
      <BaseComponent isLoading={loading} title={guide?.title}>
        <PageHeader
          buttons={
            isAuthor
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
              : []
          }
          title={guide?.title || "No title provided"}
          description={guide?.description || "No description provided"}
        />
        <GuideDetailsContent guide={guide} />
      </BaseComponent>
    </>
  );
}
