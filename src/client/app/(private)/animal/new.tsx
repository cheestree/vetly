import animalApi from "@/api/animal/animal.api";
import { AnimalCreate } from "@/api/animal/animal.input";
import AnimalCreateContent from "@/components/animal/AnimalCreateContent";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import { ImagePickerAsset } from "expo-image-picker";
import { router } from "expo-router";

export default function PetCreate() {
  const handleCreateAnimal = async (
    createdAnimal: AnimalCreate,
    image?: ImagePickerAsset | File,
  ) => {
    try {
      await animalApi.createAnimal(createdAnimal, image);
      router.back();
    } catch (error) {
      throw error;
    }
  };

  return (
    <>
      <BaseComponent title={"Add an animal"}>
        <PageHeader
          buttons={[]}
          title={"Add"}
          description={"Add an animal to the system"}
        />
        <AnimalCreateContent onCreate={handleCreateAnimal} />
      </BaseComponent>
    </>
  );
}
