import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import RequestScreenContent from "@/components/request/RequestScreenContent";
import ROUTES from "@/lib/routes";
import { router } from "expo-router";

export default function RequestScreen() {
  return (
    <BaseComponent title={"Request"}>
      <PageHeader
        title={"Requests"}
        description={"Stay up to date with requests you submit"}
        buttons={[
          {
            name: "Create a request",
            icon: "plus",
            operation: () => {
              router.navigate({
                pathname: ROUTES.PRIVATE.REQUEST.CREATE,
              });
            },
          },
          {
            name: "Search requests",
            icon: "search",
            operation: () => {},
          },
        ]}
      />
      <RequestScreenContent />
    </BaseComponent>
  );
}
