import { DocumentPickerAsset } from "expo-document-picker";
import { ImagePickerAsset } from "expo-image-picker";
import { Platform } from "react-native";

type GenericFile = File | ImagePickerAsset | DocumentPickerAsset;

type UploadFile = {
  key: string;
  file?: GenericFile | null;
};

export async function buildMultipartFormData(
  jsonKey: string,
  jsonObject: any,
  files?: UploadFile[],
): Promise<FormData> {
  const formData = new FormData();

  formData.append(
    jsonKey,
    new Blob([JSON.stringify(jsonObject)], { type: "application/json" }),
  );

  if (files) {
    for (const { key, file } of files) {
      if (!file) continue;
      if (Platform.OS === "web") {
        if (file instanceof File) {
          formData.append(key, file);
        } else {
          console.warn(
            `[FormData] Expected a File on web platform for key=${key}`,
          );
        }
      } else {
        if ("uri" in file) {
          formData.append(key, {
            uri: file.uri,
            type: file.mimeType ?? "application/octet-stream",
            name: file.file?.name ?? "upload.bin",
          } as any);
        } else {
          console.warn(
            `[FormData] Unexpected file type on native platform for key=${key}`,
          );
        }
      }
    }
  }
  return formData;
}

export default { buildMultipartFormData };
