import { ApiPaths } from "@/api/Path";
import api from "@/lib/axios";
import { RequestList } from "../RequestList";
import { buildMultipartFormData } from "../Utils";
import {
  CheckupCreate,
  CheckupQueryParams,
  CheckupUpdate,
} from "./checkup.input";
import { CheckupInformation, CheckupPreview } from "./checkup.output";

async function getCheckup(id: number): Promise<CheckupInformation> {
  const response = await api.get(ApiPaths.checkups.get(id));
  return response.data;
}

async function getCheckups(
  queryParams: CheckupQueryParams = {},
): Promise<RequestList<CheckupPreview>> {
  const response = await api.get(ApiPaths.checkups.get_all, {
    params: queryParams,
  });
  return response.data;
}

async function getTodayCheckups(): Promise<RequestList<CheckupPreview>> {
  const today = new Date();
  const isoDate = today.toISOString().split("T")[0];

  const response = await api.get(ApiPaths.checkups.get_all, {
    params: {
      dateTimeStart: isoDate,
    },
  });

  return response.data;
}

async function createCheckup(
  input: CheckupCreate,
): Promise<Map<string, number>> {
  const formData = await buildMultipartFormData("checkup", input);

  const response = await api.post(ApiPaths.checkups.create, formData);

  return response.data;
}

async function updateCheckup(
  id: number,
  input: CheckupUpdate,
  image?: File,
): Promise<void> {
  const files = image ? [{ key: "image", file: image }] : undefined;

  const formData = await buildMultipartFormData("checkup", input, files);

  const response = await api.post(ApiPaths.checkups.update(id), formData);

  return response.data;
}

async function deleteCheckup(id: number): Promise<void> {
  const response = await api.delete(ApiPaths.checkups.delete(id));
  return response.data;
}

export default {
  getCheckup,
  getCheckups,
  getTodayCheckups,
  createCheckup,
  updateCheckup,
  deleteCheckup,
};
