import { ApiPaths } from "@/api/Path";
import api from "@/lib/axios";
import { ImagePickerAsset } from "expo-image-picker";
import { Platform } from "react-native";
import { AnimalCreate, AnimalQueryParams, AnimalUpdate } from "./animal.input";
import { AnimalInformation, AnimalPreview } from "./animal.output";

async function buildAnimalFormData(
  input: AnimalCreate | AnimalUpdate,
  image?: ImagePickerAsset | File,
): Promise<FormData> {
  const formData = new FormData();

  formData.append(
    "animal",
    new Blob([JSON.stringify(input)], { type: "application/json" }),
  );

  if (image) {
    if (Platform.OS === "web") {
      if (image instanceof File) {
        formData.append("image", image);
      } else {
        console.warn("Expected a File on web platform");
      }
    } else {
      if (isImagePickerAsset(image)) {
        formData.append("image", {
          uri: image.uri,
          type: image.type ?? "image/jpeg",
          name: image.fileName ?? "image.jpg",
        } as any);
      } else {
        console.warn("Expected an ImagePickerAsset on native platform");
      }
    }
  }

  return formData;
}

function isImagePickerAsset(obj: any): obj is ImagePickerAsset {
  return obj && typeof obj.uri === "string";
}

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
  image?: ImagePickerAsset | File,
): Promise<Map<string, number>> {
  const formData = await buildAnimalFormData(input, image);

  const response = await api.post(ApiPaths.animals.create, formData);

  return response.data;
}

async function updateAnimal(
  id: number,
  input: AnimalUpdate,
  image?: ImagePickerAsset | File,
): Promise<void> {
  const formData = await buildAnimalFormData(input, image);

  const response = await api.post(ApiPaths.animals.update(id), formData);

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
