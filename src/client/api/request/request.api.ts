import { ApiPaths } from "@/api/Path";
import api from "@/lib/axios";
import { RequestList } from "../RequestList";
import {
  RequestCreate,
  RequestQueryParams,
  RequestUpdate,
  UserRequestQueryParams,
} from "./request.input";
import { RequestInformation, RequestPreview } from "./request.output";

async function getRequest(id: string): Promise<RequestInformation> {
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
  const response = await api.get(ApiPaths.requests.get_user_requests, {
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

async function updateRequest(id: string, input: RequestUpdate): Promise<void> {
  const response = await api.put(ApiPaths.requests.update(id), input);
  return response.data;
}

async function deleteRequest(id: string): Promise<void> {
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
