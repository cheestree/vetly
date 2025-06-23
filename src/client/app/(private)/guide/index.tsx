import { Role } from "@/api/user/user.output";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import { useAuth } from "@/hooks/useAuth";
import ROUTES from "@/lib/routes";
import { router } from "expo-router";

export default function GuideScreen() {
  const { hasRoles } = useAuth();
  const accessButtons = hasRoles(Role.VETERINARIAN, Role.ADMIN);

  const buttons = [
    {
      name: "Search Guides",
      icon: "search",
      operation: () => {
        router.navigate({
          pathname: ROUTES.PRIVATE.GUIDE.SEARCH,
        });
      },
    },
    ...(accessButtons
      ? [
          {
            name: "New Guide",
            icon: "plus",
            operation: () => {
              router.navigate({
                pathname: ROUTES.PRIVATE.GUIDE.CREATE,
              });
            },
          },
        ]
      : []),
  ];

  return (
    <BaseComponent title={"Guides"}>
      <PageHeader
        title={"Guides"}
        description={
          "Look up guides curated by veterinarians or create your own one"
        }
        buttons={buttons}
      />
    </BaseComponent>
  );
}
