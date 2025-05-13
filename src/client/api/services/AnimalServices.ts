import { ApiPaths } from "../http/Path";
import { buildFetchOptions, buildQueryParams } from "../Utils";

async function getAnimal(
  animalId: string,
  token: string,
): Promise<AnimalInformation> {
  return fetch(
    ApiPaths.animals.get(animalId),
    buildFetchOptions("GET", undefined, token),
  )
    .then((response) => response.json())
    .catch((error) => {
      console.error("Error fetching animal:", error);
      throw error;
    });
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
  token: string,
): Promise<RequestList<AnimalPreview>> {
  const query = buildQueryParams(queryParams);

  return fetch(
    ApiPaths.animals.get_all + query,
    buildFetchOptions("GET", undefined, token),
  )
    .then((response) => response.json())
    .catch((error) => {
      console.error("Error fetching animals:", error);
      throw error;
    });
}

export default { getAnimal, getAllAnimals };
