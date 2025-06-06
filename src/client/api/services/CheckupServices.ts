import { ApiPaths } from "../http/Path";
import api from "@/lib/axios";

async function getCheckup(checkupId: string): Promise<CheckupInformation> {
  const response = await api.get(ApiPaths.checkups.get(checkupId));
  return response.data;
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
  const response = await api.get(ApiPaths.checkups.get_all, {
    params: queryParams,
  });
  return response.data;
}

export default { getCheckup, getCheckups };
