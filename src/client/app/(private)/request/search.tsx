import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import RequestSearchContent from "@/components/request/RequestSearchContent";

export default function RequestSearchScreen() {
  return (
    <BaseComponent title={"Requests"}>
      <PageHeader
        title={"Requests"}
        description={"Consult the requests you've made"}
      />
      <RequestSearchContent />
    </BaseComponent>
  );
}
