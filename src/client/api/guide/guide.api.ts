import { ApiPaths } from "@/api/Path";
import api from "@/lib/axios";
import { GuideCreate, GuideQueryParams, GuideUpdate } from "./guide.input";
import { GuideInformation, GuidePreview } from "./guide.output";

function buildGuideFormData(
  input: GuideCreate | GuideUpdate,
  image?: File,
): FormData {
  const formData = new FormData();

  formData.append(
    "guide",
    new Blob([JSON.stringify(input)], { type: "application/json" }),
  );

  if (image) {
    formData.append("image", image);
  }

  return formData;
}

async function getGuide(id: number): Promise<GuideInformation> {
  const response = await api.get(ApiPaths.guides.get(id));
  return response.data;
}

async function getGuides(
  queryParams: GuideQueryParams = {},
): Promise<RequestList<GuidePreview>> {
  const response = await api.get(ApiPaths.guides.get_all, {
    params: queryParams,
  });
  return response.data;
}

async function createGuide(
  input: GuideCreate,
  image?: File,
): Promise<Map<string, number>> {
  const formData = buildGuideFormData(input, image);

  const response = await api.post(ApiPaths.guides.create, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });

  return response.data;
}

async function updateGuide(id: number, input: GuideUpdate): Promise<void> {
  const response = await api.put(ApiPaths.guides.update(id), input);
  return response.data;
}

async function deleteGuide(id: number): Promise<void> {
  const response = await api.delete(ApiPaths.guides.delete(id));
  return response.data;
}

export default { getGuide, getGuides, createGuide, updateGuide, deleteGuide };
