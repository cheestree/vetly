import { ApiPaths } from "../http/Path";
import { buildQueryParams, buildFetchOptions } from "../Utils";

async function getCheckup(
  checkupId: string,
  token: string,
): Promise<CheckupInformation> {
  return fetch(
    ApiPaths.checkups.get(checkupId),
    buildFetchOptions("GET", undefined, token),
  )
    .then((response) => response.json())
    .catch((error) => {
      console.error("Error fetching checkup:", error);
      throw error;
    });
}

type CheckupQueryParams = {
  veterinarianId?: number;
  veterinarianName?: string;
  animalId?: number;
  animalName?: string;
  clinicId?: number;
  clinicName?: string;
  dateTimeStart?: string;
  dateTimeEnd?: string;
  page?: number;
  limit?: number;
  sortBy?: string;
  sortOrder?: "asc" | "desc";
};

async function getCheckups(
  queryParams: CheckupQueryParams = {},
  token: string,
): Promise<RequestList<CheckupPreview>> {
  const query = buildQueryParams(queryParams);

  return fetch(
    ApiPaths.checkups.get_all + query,
    buildFetchOptions("GET", undefined, token),
  )
    .then((response) => response.json())
    .catch((error) => {
      console.error("Error fetching user profile:", error);
      throw error;
    });
}

export default { getCheckup, getCheckups };
