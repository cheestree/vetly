import { ApiPaths } from "@/api/Path";
import api from "@/lib/axios";
import { RequestList } from "../RequestList";
import { buildMultipartFormData } from "../Utils";
import { ClinicCreate, ClinicQueryParams, ClinicUpdate } from "./clinic.input";
import { ClinicInformation, ClinicPreview } from "./clinic.output";

async function getClinic(id: number): Promise<ClinicInformation> {
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
  input: ClinicCreate,
  image?: File,
): Promise<Map<string, number>> {
  const files = image ? [{ key: "image", file: image }] : undefined;

  const formData = await buildMultipartFormData("clinic", input, files);

  const response = await api.post(ApiPaths.clinics.create, formData);

  return response.data;
}

async function updateClinic(
  id: number,
  input: ClinicUpdate,
  image?: File,
): Promise<void> {
  const files = image ? [{ key: "image", file: image }] : undefined;

  const formData = await buildMultipartFormData("clinic", input, files);

  const response = await api.post(ApiPaths.clinics.update(id), formData);

  return response.data;
}

async function deleteClinic(id: number): Promise<void> {
  const response = await api.delete(ApiPaths.clinics.delete(id));
  return response.data;
}

export default {
  getClinic,
  getClinics,
  createClinic,
  updateClinic,
  deleteClinic,
};
