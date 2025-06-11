import { ApiPaths } from "@/api/Path";
import api from "@/lib/axios";

async function getRequest(id: number): Promise<RequestInformation> {
  const response = await api.get(ApiPaths.requests.get(id));
  return response.data;
}

async function getRequests(
  queryParams: RequestQueryParams = {},
): Promise<RequestList<RequestPreview>> {
  const response = await api.get(ApiPaths.requests.get_all, {
    params: queryParams,
  });
  return response.data;
}

async function getUserRequests(
  queryParams: UserRequestQueryParams = {},
): Promise<RequestList<RequestPreview>> {
  const response = await api.get(ApiPaths.requests.get_all, {
    params: queryParams,
  });
  return response.data;
}

async function createRequest(
  input: RequestCreate,
): Promise<Map<string, number>> {
  const response = await api.post(ApiPaths.requests.create, input);
  return response.data;
}

async function updateRequest(id: number, input: RequestUpdate): Promise<void> {
  const response = await api.put(ApiPaths.requests.update(id), input);
  return response.data;
}

async function deleteRequest(id: number): Promise<void> {
  const response = await api.delete(ApiPaths.requests.delete(id));
  return response.data;
}

export default {
  getRequest,
  getRequests,
  getUserRequests,
  createRequest,
  updateRequest,
  deleteRequest,
};
