import requestApi from "@/api/request/request.api";
import { RequestCreate } from "@/api/request/request.input";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import RequestCreateContent from "@/components/request/RequestCreateContent";
import { router } from "expo-router";

export default function RequestCreateScreen() {
  const handleCreateRequest = async (createdRequest: RequestCreate) => {
    try {
      await requestApi.createRequest(createdRequest);
      router.back();
    } catch (error) {
      throw error;
    }
  };

  return (
    <BaseComponent title={"Create a request"}>
      <PageHeader
        buttons={[]}
        title={"Create"}
        description={"Create a request"}
      />
      <RequestCreateContent onCreate={handleCreateRequest} />
    </BaseComponent>
  );
}
