import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import ROUTES from "@/lib/routes";
import { router } from "expo-router";

export default function ClinicScreen() {
  return (
    <>
      <BaseComponent title={"Clinics"}>
        <PageHeader
          title={"Clinics"}
          description={"Look up clinics near you or request to create your own"}
          buttons={[
            {
              name: "New Clinic",
              icon: "plus",
              operation: () => {
                router.navigate({
                  pathname: ROUTES.PRIVATE.CLINIC.CREATE,
                });
              },
            },
            {
              name: "Search Clinics",
              icon: "search",
              operation: () => {
                router.navigate({
                  pathname: ROUTES.PRIVATE.CLINIC.SEARCH,
                });
              },
            },
          ]}
        />
      </BaseComponent>
    </>
  );
}
