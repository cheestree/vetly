import { Role } from "@/api/user/user.output";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import { useAuth } from "@/hooks/useAuth";
import ROUTES from "@/lib/routes";
import { router } from "expo-router";
import React from "react";

export default function CheckupScreen() {
  const { hasRoles } = useAuth();
  const accessButtons = hasRoles(Role.VETERINARIAN, Role.ADMIN);

  const buttons = [
    {
      name: "Search Checkups",
      icon: "search",
      operation: () => {
        router.navigate({
          pathname: ROUTES.PRIVATE.CHECKUP.SEARCH,
        });
      },
    },
    ...(accessButtons
      ? [
          {
            name: "New Checkup",
            icon: "plus",
            operation: () => {
              router.navigate({
                pathname: ROUTES.PRIVATE.CHECKUP.CREATE,
              });
            },
          },
        ]
      : []),
  ];

  return (
    <BaseComponent title={"Checkups"}>
      <PageHeader
        title={"Checkups"}
        description={"Manage checkups"}
        buttons={buttons}
      />
    </BaseComponent>
  );
}
