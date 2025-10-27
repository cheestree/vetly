import { ApiPaths } from "@/api/Path";
import api from "@/lib/axios";
import { ImagePickerAsset } from "expo-image-picker";
import { RequestList } from "../RequestList";
import { buildMultipartFormData } from "../Utils";
import { AnimalCreate, AnimalQueryParams, AnimalUpdate } from "./animal.input";
import { AnimalInformation, AnimalPreview } from "./animal.output";

async function getAnimal(id: number): Promise<AnimalInformation> {
  const response = await api.get(ApiPaths.animals.get(id));
  return response.data;
}

async function getAllAnimals(
  queryParams: AnimalQueryParams = {},
): Promise<RequestList<AnimalPreview>> {
  const response = await api.get(ApiPaths.animals.get_all, {
    params: queryParams,
  });
  return response.data;
}

async function createAnimal(
  input: AnimalCreate,
  image: ImagePickerAsset | File | null,
): Promise<Map<string, number>> {
  const files = image ? [{ key: "image", file: image }] : undefined;

  const formData = await buildMultipartFormData("animal", input, files);

  const response = await api.post(ApiPaths.animals.create, formData);

  return response.data;
}

async function updateAnimal(
  id: number,
  input: AnimalUpdate,
  image: ImagePickerAsset | File | null,
): Promise<void> {
  const files = image ? [{ key: "image", file: image }] : undefined;

  const formData = await buildMultipartFormData("animal", input, files);

  const response = await api.patch(ApiPaths.animals.update(id), formData);

  return response.data;
}

async function deleteAnimal(id: number): Promise<void> {
  const response = await api.delete(ApiPaths.animals.delete(id));
  return response.data;
}

export default {
  getAnimal,
  getAllAnimals,
  createAnimal,
  updateAnimal,
  deleteAnimal,
};
