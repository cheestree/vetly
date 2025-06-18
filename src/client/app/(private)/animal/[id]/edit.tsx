import animalApi from "@/api/animal/animal.api";
import { AnimalUpdate } from "@/api/animal/animal.input";
import { AnimalInformation } from "@/api/animal/animal.output";
import AnimalEditForm from "@/components/animal/AnimalEditContent";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import { useResource } from "@/hooks/useResource";
import { ImagePickerAsset } from "expo-image-picker";
import { useLocalSearchParams, useRouter } from "expo-router";
import { useCallback } from "react";

export default function PetEdit() {
  const { id } = useLocalSearchParams();
  const numericId = Number(id);
  const router = useRouter();

  const fetchAnimal = useCallback(
    () => animalApi.getAnimal(numericId),
    [numericId],
  );

  const { data: animal, loading } = useResource<AnimalInformation>(fetchAnimal);

  const handleSave = async (
    updatedAnimal: Partial<AnimalUpdate>,
    image?: ImagePickerAsset | File,
  ) => {
    try {
      await animalApi.updateAnimal(numericId, updatedAnimal, image);
      router.back();
    } catch (error) {
      throw error;
    }
  };

  return (
    <BaseComponent
      isLoading={loading && !animal}
      title={animal?.name + " - Edit" || "Animal Edit"}
    >
      <PageHeader
        buttons={[]}
        title={"Edit"}
        description={animal?.name || "Animal"}
      />
      <AnimalEditForm animal={animal} onSave={handleSave} loading={loading} />
    </BaseComponent>
  );
}
