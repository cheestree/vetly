import { ApiPaths } from "@/api/Path";
import api from "@/lib/axios";

async function getGuide(id: number): Promise<GuideInformation> {
  const response = await api.get(ApiPaths.guides.get(id));
  return response.data;
}

async function getGuides(
  queryParams: GuideQueryParams = {},
): Promise<RequestList<GuidePreview>> {
  const response = await api.get(ApiPaths.guides.get_all, {
    params: queryParams,
  });
  return response.data;
}

async function createGuide(input: GuideCreate): Promise<Map<string, number>> {
  const response = await api.post(ApiPaths.guides.create, input);
  return response.data;
}

async function updateGuide(id: number, input: GuideUpdate): Promise<void> {
  const response = await api.put(ApiPaths.guides.update(id), input);
  return response.data;
}

async function deleteGuide(id: number): Promise<void> {
  const response = await api.delete(ApiPaths.guides.delete(id));
  return response.data;
}

export default { getGuide, getGuides, createGuide, updateGuide, deleteGuide };
