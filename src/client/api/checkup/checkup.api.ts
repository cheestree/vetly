import { ApiPaths } from "@/api/Path";
import api from "@/lib/axios";
import {
  CheckupCreate,
  CheckupQueryParams,
  CheckupUpdate,
} from "./checkup.input";
import { CheckupInformation, CheckupPreview } from "./checkup.output";

function buildCheckupFormData(
  input: CheckupCreate | CheckupUpdate,
  image?: File,
): FormData {
  const formData = new FormData();

  formData.append(
    "checkup",
    new Blob([JSON.stringify(input)], { type: "application/json" }),
  );

  if (image) {
    formData.append("image", image);
  }

  return formData;
}

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
  image?: File,
): Promise<Map<string, number>> {
  const formData = buildCheckupFormData(input, image);

  const response = await api.post(ApiPaths.checkups.create, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });

  return response.data;
}

async function updateCheckup(
  id: number,
  input: CheckupUpdate,
  image?: File,
): Promise<void> {
  const formData = buildCheckupFormData(input, image);

  const response = await api.post(ApiPaths.checkups.update(id), formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });

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
