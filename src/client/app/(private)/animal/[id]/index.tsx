import animalApi from "@/api/animal/animal.api";
import AnimalDetailsContent from "@/components/animal/AnimalDetailsContent";
import BaseComponent from "@/components/basic/BaseComponent";
import { useResource } from "@/hooks/useResource";
import ROUTES from "@/lib/routes";
import { router, Stack, useLocalSearchParams } from "expo-router";

export default function PetDetails() {
  const { id } = useLocalSearchParams();
  const numericId = Number(id);

  const { data: animal, loading } = useResource<AnimalInformation>(() =>
    animalApi.getAnimal(numericId),
  );

  return (
    <>
      <Stack.Screen options={{ title: animal?.name }} />
      <BaseComponent
        isLoading={loading}
        title={animal?.name || "Animal Details"}
      >
        <AnimalDetailsContent
          animal={animal}
          deleteAnimal={async () => {
            await animalApi.deleteAnimal(numericId);
            router.back();
          }}
          editAnimal={() => {
            router.navigate({
              pathname: ROUTES.PRIVATE.ANIMAL.EDIT,
              params: { id: animal?.id },
            });
          }}
        />
      </BaseComponent>
    </>
  );
}
