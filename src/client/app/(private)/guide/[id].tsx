import BaseComponent from "@/components/basic/BaseComponent";
import { useResource } from "@/hooks/useResource";
import { useLocalSearchParams, Stack } from "expo-router";
import React from "react";
import guideApi from "@/api/guide/guide.api";
import GuideDetailsContent from "@/components/guide/GuideDetailsContent";

export default function GuideDetails() {
  const { id } = useLocalSearchParams();
  const numericId = Number(id);

  const { data: guide, loading } = useResource<GuideInformation>(() =>
    guideApi.getGuide(numericId),
  );

  return (
    <>
      <Stack.Screen options={{ title: guide?.title }} />
      <BaseComponent isLoading={loading} title={guide?.title}>
        <GuideDetailsContent guide={guide} />
      </BaseComponent>
    </>
  );
}
