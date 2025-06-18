import { useThemedStyles } from "@/hooks/useThemedStyles";
import { ChangeEvent, useRef } from "react";
import { Platform, View } from "react-native";
import CustomButton from "./CustomButton";
import CustomImage from "./CustomImage";

type CustomImagePickerProps = {
  handleWebFileChange: (e: ChangeEvent<HTMLInputElement>) => void;
  handleNativeFileChange: () => Promise<void>;
  handleRemoveImage: () => void;
  imagePreviewUrl?: string | null;
  loading: boolean;
};

export default function CustomImagePicker({
  handleWebFileChange,
  handleNativeFileChange,
  handleRemoveImage,
  imagePreviewUrl,
  loading,
}: CustomImagePickerProps) {
  const { styles } = useThemedStyles();
  const isWeb = Platform.OS === "web";
  const fileInputRef = useRef<HTMLInputElement | null>(null);

  const openFilePicker = () => {
    fileInputRef.current?.click();
  };

  return (
    <View style={styles.innerContainer}>
      <CustomImage url={imagePreviewUrl} />

      {isWeb ? (
        <>
          <CustomButton
            text="Select Image"
            onPress={openFilePicker}
            disabled={loading}
          />
          <input
            aria-label="image"
            ref={fileInputRef}
            type="file"
            accept="image/*"
            onChange={handleWebFileChange}
            disabled={loading}
            style={{ display: "none" }}
          />
        </>
      ) : (
        <CustomButton
          text="Select Image"
          onPress={handleNativeFileChange}
          disabled={loading}
        />
      )}

      <CustomButton
        onPress={handleRemoveImage}
        text={"Remove Image"}
        disabled={loading}
      />
    </View>
  );
}
