import { Role } from "@/api/user/user.output";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import { useAuth } from "@/hooks/useAuth";
import ROUTES from "@/lib/routes";
import { router } from "expo-router";
import React from "react";

export default function PetScreen() {
  const { hasRoles } = useAuth();
  const accessButtons = hasRoles(Role.VETERINARIAN, Role.ADMIN);

  const buttons = [
    {
      name: "Search Pets",
      icon: "search",
      operation: () =>
        router.navigate({ pathname: ROUTES.PRIVATE.ANIMAL.SEARCH }),
    },
    ...(accessButtons
      ? [
          {
            name: "New Pet",
            icon: "plus",
            operation: () =>
              router.navigate({ pathname: ROUTES.PRIVATE.ANIMAL.CREATE }),
          },
        ]
      : []),
  ];

  return (
    <BaseComponent title="Pets">
      <PageHeader
        title="Pets"
        description="Manage patients' pets"
        buttons={buttons}
      />
    </BaseComponent>
  );
}
