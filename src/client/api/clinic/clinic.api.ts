import { ApiPaths } from "@/api/Path";
import api from "@/lib/axios";
import { ClinicCreate, ClinicQueryParams, ClinicUpdate } from "./clinic.input";

function buildClinicFormData(
  input: ClinicCreate | ClinicUpdate,
  image?: File,
): FormData {
  const formData = new FormData();

  formData.append(
    "clinic",
    new Blob([JSON.stringify(input)], { type: "application/json" }),
  );

  if (image) {
    formData.append("image", image);
  }

  return formData;
}

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
  const formData = buildClinicFormData(input, image);

  const response = await api.post(ApiPaths.clinics.create, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });

  return response.data;
}

async function updateClinic(
  id: number,
  input: ClinicUpdate,
  image?: File,
): Promise<void> {
  const formData = buildClinicFormData(input, image);

  const response = await api.post(ApiPaths.clinics.update(id), formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });

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
