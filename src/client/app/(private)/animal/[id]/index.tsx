import animalApi from "@/api/animal/animal.api";
import { AnimalInformation } from "@/api/animal/animal.output";
import { Role } from "@/api/user/user.output";
import AnimalDetailsContent from "@/components/animal/AnimalDetailsContent";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import { useAuth } from "@/hooks/useAuth";
import { useResource } from "@/hooks/useResource";
import ROUTES from "@/lib/routes";
import {
  router,
  Stack,
  useFocusEffect,
  useLocalSearchParams,
} from "expo-router";
import { useCallback } from "react";

export default function PetDetailsScreen() {
  const { id } = useLocalSearchParams();
  const numericId = Number(id);
  const { hasRoles } = useAuth();

  const fetchAnimal = useCallback(
    () => animalApi.getAnimal(numericId),
    [numericId],
  );

  const {
    data: animal,
    loading,
    refetch,
  } = useResource<AnimalInformation>(fetchAnimal);

  const handleDeleteAnimal = async () => {
    await animalApi.deleteAnimal(numericId);
    router.back();
  };

  const handleEditAnimal = async () => {
    router.navigate({
      pathname: ROUTES.PRIVATE.ANIMAL.EDIT,
      params: { id: numericId },
    });
  };

  const handleCreateCheckup = async () => {
    router.navigate({
      pathname: ROUTES.PRIVATE.CHECKUP.CREATE,
      params: { animalId: numericId },
    });
  };

  useFocusEffect(
    useCallback(() => {
      refetch();
    }, [refetch]),
  );

  return (
    <>
      <Stack.Screen options={{ title: animal?.name }} />
      <BaseComponent
        isLoading={loading}
        title={animal?.name || "Animal Details"}
      >
        <PageHeader
          buttons={
            hasRoles(Role.ADMIN, Role.VETERINARIAN)
              ? [
                  {
                    name: "Create checkup",
                    icon: "plus",
                    operation: handleCreateCheckup,
                  },
                  {
                    name: "Delete",
                    icon: "trash",
                    operation: handleDeleteAnimal,
                  },
                  {
                    name: "Edit",
                    icon: "pen",
                    operation: handleEditAnimal,
                  },
                ]
              : []
          }
          title={"Details"}
          description={animal?.name}
        />
        <AnimalDetailsContent animal={animal} />
      </BaseComponent>
    </>
  );
}
