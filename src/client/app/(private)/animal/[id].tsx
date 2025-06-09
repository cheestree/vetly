import { Stack, useLocalSearchParams } from "expo-router";
import BaseComponent from "@/components/basic/BaseComponent";
import animalApi from "@/api/animal/animal.api"
import AnimalDetailsContent from "@/components/animal/AnimalDetailsContent";
import { useResource } from "@/hooks/useResource";

export default function PetDetails() {
  const { id } = useLocalSearchParams();
  const numericId = Number(id);

  const { data: animal, loading } = useResource<AnimalInformation>(() =>
    animalApi.getAnimal(numericId)
  );

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
