import { Role } from "@/api/user/user.output";
import AnimalSearchContent from "@/components/animal/AnimalSearchContent";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import { useAuth } from "@/hooks/useAuth";
import ROUTES from "@/lib/routes";
import { router } from "expo-router";
import React from "react";

export default function PetSearchScreen() {
  const { hasRoles } = useAuth();

  return (
    <BaseComponent title={"Search Pets"}>
      <PageHeader
        title={"Pets"}
        description={"Search patient's pets"}
        buttons={
          hasRoles(Role.VETERINARIAN, Role.ADMIN)
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
