import { Stack, useLocalSearchParams } from "expo-router";
import AnimalServices from "@/api/services/AnimalServices";
import BaseComponent from "@/components/basic/BaseComponent";
import { useEffect, useState } from "react";
import AnimalDetailsContent from "@/components/animal/AnimalDetailsContent";
import { useResource } from "@/hooks/useResource";

export default function PetDetails() {
  const { id } = useLocalSearchParams();
  const { data: animal, loading } = useResource<AnimalInformation>(() => AnimalServices.getAnimal(id[0]));

  return (
    <>
      <Stack.Screen options={{ title: animal?.name }} />
      <BaseComponent
        isLoading={loading}
        title={animal?.name || "Animal Details"}
      >
        <AnimalDetailsContent animal={animal} />
      </BaseComponent>
    </>
  );
}
