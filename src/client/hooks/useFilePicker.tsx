import * as DocumentPicker from "expo-document-picker";
import * as ImagePicker from "expo-image-picker";
import { useState } from "react";
import { Toast } from "toastify-react-native";

type FilePickerProps = {
  initialPreviewUrl?: string | null;
};

export function useFilePicker({
  initialPreviewUrl = null,
}: FilePickerProps = {}) {
  const [previewUrl, setPreviewUrl] = useState<string | null>(
    initialPreviewUrl,
  );

  const pickFile = async (
    onPicked: (file: DocumentPicker.DocumentPickerAsset | File) => void,
  ) => {
    try {
      const result = await DocumentPicker.getDocumentAsync({
        type: "application/pdf",
        multiple: false,
        copyToCacheDirectory: true,
      });

      if (!result.canceled && result.assets?.[0]) {
        onPicked(result.assets[0]);
      }
    } catch (e) {
      Toast.error("Failed to pick file");
    }
  };

  const pickImage = async (
    onPicked: (image: ImagePicker.ImagePickerAsset | File) => void,
  ) => {
    try {
      const result = await ImagePicker.launchImageLibraryAsync({
        mediaTypes: ["images"],
        allowsEditing: true,
        aspect: [4, 3],
        quality: 1,
        selectionLimit: 1,
      });

      if (!result.canceled && result.assets?.[0]) {
        const file = result.assets[0];
        onPicked(file);
        setPreviewUrl(file.uri);
      }
    } catch (e) {
      Toast.error("Failed to pick image");
    }
  };

  const handleWebImageChange = (
    e: React.ChangeEvent<HTMLInputElement>,
    onPicked: (file: File) => void,
  ) => {
    const file = e.target.files?.[0];
    if (file) {
      onPicked(file);
      setPreviewUrl(URL.createObjectURL(file));
    }
  };

  const clearPreview = () => setPreviewUrl(null);

  return {
    pickFile,
    pickImage,
    handleWebImageChange,
    previewUrl,
    setPreviewUrl,
    clearPreview,
  };
}
