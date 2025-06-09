import { ApiPaths } from "@/api/Path";
import api from "@/lib/axios";

async function getClinic(
  id: number
): Promise<ClinicInformation> {
  const response = await api.get(ApiPaths.clinics.get(id));
  return response.data;
}

async function getClinics(
  queryParams: ClinicQueryParams = {},
): Promise<RequestList<ClinicPreview>> {
  const response = await api.get(ApiPaths.clinics.get_all, {
    params: queryParams,
  });
  return response.data;
}

async function createClinic(
  input: ClinicCreate
): Promise<Map<string, number>> {
  const response = await api.post(ApiPaths.clinics.create, input);
  return response.data
}

async function updateClinic(
  id: number,
  input: ClinicUpdate
): Promise<void> {
  const response = await api.put(ApiPaths.clinics.update(id), input)
  return response.data
}

async function deleteClinic(
  id: number
): Promise<void> {
  const response = await api.delete(ApiPaths.clinics.delete(id))
  return response.data
}

export default { getClinic, getClinics, createClinic, updateClinic, deleteClinic };
