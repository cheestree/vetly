import { Role } from "@/api/user/user.output";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import { useAuth } from "@/hooks/useAuth";
import ROUTES from "@/lib/routes";
import { router } from "expo-router";

export default function ClinicScreen() {
  const { hasRoles } = useAuth();
  const accessButtons = hasRoles(Role.VETERINARIAN, Role.ADMIN);

  const buttons = [
    {
      name: "Search Clinics",
      icon: "search",
      operation: () => {
        router.navigate({
          pathname: ROUTES.PRIVATE.CLINIC.SEARCH,
        });
      },
    },
    ...(accessButtons
      ? [
          {
            name: "New Clinic",
            icon: "plus",
            operation: () => {
              router.navigate({
                pathname: ROUTES.PRIVATE.CLINIC.CREATE,
              });
            },
          },
        ]
      : []),
  ];

  return (
    <BaseComponent title={"Clinics"}>
      <PageHeader
        title={"Clinics"}
        description={"Look up clinics near you or request to create your own"}
        buttons={buttons}
      />
    </BaseComponent>
  );
}
