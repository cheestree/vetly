import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import ROUTES from "@/lib/routes";
import { router } from "expo-router";

export default function GuideScreen() {
  return (
    <>
      <BaseComponent title={"Guides"}>
        <PageHeader
          title={"Guides"}
          description={
            "Look up guides curated by veterinarians or create your own one"
          }
          buttons={[
            {
              name: "New Guide",
              icon: "plus",
              operation: () => {
                router.navigate({
                  pathname: ROUTES.PRIVATE.GUIDE.CREATE,
                });
              },
            },
            {
              name: "Search Guides",
              icon: "search",
              operation: () => {
                router.navigate({
                  pathname: ROUTES.PRIVATE.GUIDE.SEARCH,
                });
              },
            },
          ]}
        />
      </BaseComponent>
    </>
  );
}
