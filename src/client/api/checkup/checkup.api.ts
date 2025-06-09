import { ApiPaths } from "@/api/Path";
import api from "@/lib/axios";

async function getCheckup(
  id: number
): Promise<CheckupInformation> {
  const response = await api.get(ApiPaths.checkups.get(id));
  return response.data;
}

async function getCheckups(
  queryParams: CheckupQueryParams = {},
): Promise<RequestList<CheckupPreview>> {
  const response = await api.get(ApiPaths.checkups.get_all, {
    params: queryParams,
  });
  return response.data;
}

async function createCheckup(
  input: CheckupCreate
): Promise<Map<string, number>> {
  const response = await api.post(ApiPaths.checkups.create, input);
  return response.data
}

async function updateCheckup(
  id: number,
  input: CheckupUpdate
): Promise<void> {
  const response = await api.put(ApiPaths.checkups.update(id), input)
  return response.data
}

async function deleteCheckup(
  id: number
): Promise<void> {
  const response = await api.delete(ApiPaths.checkups.delete(id))
  return response.data
}

export default { getCheckup, getCheckups, createCheckup, updateCheckup, deleteCheckup };
