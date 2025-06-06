import { ApiPaths } from "../http/Path";
import api from "@/lib/axios";

async function getAnimal(animalId: string): Promise<AnimalInformation> {
  const response = await api.get(ApiPaths.animals.get(animalId));
  return response.data;
}

type AnimalQueryParams = {
  userId?: string;
  name?: string;
  microchip?: string;
  birthDate?: number;
  species?: number;
  owned?: boolean;
  self?: boolean;
  page?: number;
  limit?: number;
  sortBy?: string;
  sortOrder?: "asc" | "desc";
};

async function getAllAnimals(
  queryParams: AnimalQueryParams = {},
): Promise<RequestList<AnimalPreview>> {
  const response = await api.get(ApiPaths.animals.get_all, {
    params: queryParams,
  });
  return response.data;
}

export default { getAnimal, getAllAnimals };
