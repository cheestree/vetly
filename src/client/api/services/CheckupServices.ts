import { ApiPaths } from "../http/Path";
import { buildQueryParams, buildFetchOptions } from "../Utils";
import auth from "../../lib/firebase";

async function getCheckup(checkupId: string): Promise<CheckupInformation> {

  const idToken = await auth.auth.currentUser?.getIdToken(false);
  
  return fetch(ApiPaths.root + ApiPaths.checkups.get(checkupId), buildFetchOptions("GET", undefined, idToken))
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
): Promise<RequestList<CheckupPreview>> {
  const query = buildQueryParams(queryParams);
  const idToken = await auth.auth.currentUser?.getIdToken(false);

  return fetch(ApiPaths.root + ApiPaths.checkups.get_all + query, buildFetchOptions("GET", undefined, idToken))
    .then((response) => response.json())
    .catch((error) => {
    console.error("Error fetching user profile:", error);
    throw error;
  });
}

export default { getCheckup, getCheckups };
