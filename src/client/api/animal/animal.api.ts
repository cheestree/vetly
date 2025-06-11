import { ApiPaths } from "@/api/Path";
import api from "@/lib/axios";

async function getAnimal(id: number): Promise<AnimalInformation> {
  const response = await api.get(ApiPaths.animals.get(id));
  return response.data;
}

async function getAllAnimals(
  queryParams: AnimalQueryParams = {},
): Promise<RequestList<AnimalPreview>> {
  const response = await api.get(ApiPaths.animals.get_all, {
    params: queryParams,
  });
  return response.data;
}

async function createAnimal(input: AnimalCreate): Promise<Map<string, number>> {
  const response = await api.post(ApiPaths.animals.create, input);
  return response.data;
}

async function updateAnimal(id: number, input: AnimalUpdate): Promise<void> {
  const response = await api.put(ApiPaths.animals.update(id), input);
  return response.data;
}

async function deleteAnimal(id: number): Promise<void> {
  const response = await api.delete(ApiPaths.animals.delete(id));
  return response.data;
}

export default {
  getAnimal,
  getAllAnimals,
  createAnimal,
  updateAnimal,
  deleteAnimal,
};
