import animalApi from "@/api/animal/animal.api";
import { AnimalUpdate } from "@/api/animal/animal.input";
import { AnimalInformation } from "@/api/animal/animal.output";
import AnimalEditContent from "@/components/animal/AnimalEditContent";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import { useResource } from "@/hooks/useResource";
import { ImagePickerAsset } from "expo-image-picker";
import { router, useLocalSearchParams } from "expo-router";
import { useCallback } from "react";

export default function PetEditScreen() {
  const { id } = useLocalSearchParams();
  const numericId = Number(id);

  const fetchAnimal = useCallback(
    () => animalApi.getAnimal(numericId),
    [numericId],
  );

  const { data: animal, loading } = useResource<AnimalInformation>(fetchAnimal);

  const handleSave = async (
    updatedAnimal: AnimalUpdate,
    image: ImagePickerAsset | File | null,
  ) => {
    await animalApi.updateAnimal(numericId, updatedAnimal, image);
    router.back();
  };

  return (
    <BaseComponent
      isLoading={loading && !animal}
      title={animal?.name + " - Edit" || "Animal Edit"}
    >
      <PageHeader title={"Edit"} description={animal?.name || "Animal"} />
      <AnimalEditContent
        animal={animal}
        onSave={handleSave}
        loading={loading}
      />
    </BaseComponent>
  );
}
