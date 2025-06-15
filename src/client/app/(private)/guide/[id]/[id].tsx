import guideApi from "@/api/guide/guide.api";
import BaseComponent from "@/components/basic/BaseComponent";
import GuideDetailsContent from "@/components/guide/GuideDetailsContent";
import { useResource } from "@/hooks/useResource";
import { Stack, useLocalSearchParams } from "expo-router";
import React, { useCallback } from "react";

export default function GuideDetails() {
  const { id } = useLocalSearchParams();
  const numericId = Number(id);

  const fetchGuide = useCallback(
    () => guideApi.getGuide(numericId),
    [numericId],
  );

  const { data: guide, loading } = useResource<GuideInformation>(fetchGuide);

  return (
    <>
      <Stack.Screen options={{ title: guide?.title }} />
      <BaseComponent isLoading={loading} title={guide?.title}>
        <GuideDetailsContent guide={guide} />
      </BaseComponent>
    </>
  );
}
