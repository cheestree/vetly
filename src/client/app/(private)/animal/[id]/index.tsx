import animalApi from "@/api/animal/animal.api";
import { AnimalInformation } from "@/api/animal/animal.output";
import { Role } from "@/api/user/user.output";
import AnimalDetailsContent from "@/components/animal/AnimalDetailsContent";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import { useAuth } from "@/hooks/useAuth";
import { useNumericRouteParam } from "@/hooks/useRouteParam";
import { useResource } from "@/hooks/useResource";
import ROUTES, { routeWithId } from "@/lib/routes";
import { router, useFocusEffect } from "expo-router";
import { useCallback } from "react";
import { Toast } from "toastify-react-native";

export default function PetDetailsScreen() {
  const numericId = useNumericRouteParam("id");
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
    try {
      await animalApi.deleteAnimal(numericId);
      router.back();
    } catch (e) {
      Toast.error("Failed to delete pet.");
    }
  };

  const handleEditAnimal = async () => {
    router.navigate(routeWithId(ROUTES.PRIVATE.ANIMAL.EDIT, numericId));
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
    <BaseComponent isLoading={loading} title={animal?.name || "Animal Details"}>
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
  );
}
