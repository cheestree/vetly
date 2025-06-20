import { Role } from "@/api/user/user.output";
import AnimalSearchContent from "@/components/animal/AnimalSearchContent";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import { useAuth } from "@/hooks/useAuth";
import ROUTES from "@/lib/routes";
import { hasRole } from "@/lib/utils";
import { router } from "expo-router";
import React from "react";

export default function PetSearchScreen() {
  const { information } = useAuth();

  const canCreatePet = hasRole(
    information.roles,
    Role.VETERINARIAN,
    Role.ADMIN,
  );

  return (
    <BaseComponent title={"Search Pets"}>
      <PageHeader
        title={"Pets"}
        description={"Search patient's pets"}
        buttons={
          canCreatePet
            ? [
                {
                  name: "New Pet",
                  icon: "plus",
                  operation: () => {
                    router.navigate({
                      pathname: ROUTES.PRIVATE.ANIMAL.CREATE,
                    });
                  },
                },
              ]
            : []
        }
      />
      <AnimalSearchContent />
    </BaseComponent>
  );
}
