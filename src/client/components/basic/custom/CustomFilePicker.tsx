import React from "react";
import { Platform, View } from "react-native";
import CustomButton from "./CustomButton";
import CustomTextInput from "./CustomTextInput";

type CustomFilePickerProps = {
  file: File | { name: string } | null;
  onPick: () => void;
  onWebPick?: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onRemove?: () => void;
  label?: string;
  accept?: string;
  loading?: boolean;
};

export default function CustomFilePicker({
  file,
  onPick,
  onWebPick,
  onRemove,
  label = "File",
  accept = "application/pdf",
  loading = false,
}: CustomFilePickerProps) {
  const isWeb = Platform.OS === "web";

  return (
    <View
      style={{ marginBottom: 16, flexDirection: "row", alignItems: "center" }}
    >
      <CustomButton
        text={file ? "Change File" : `Add ${label}`}
        onPress={onPick}
        disabled={loading}
      />
      {isWeb && onWebPick && (
        <input
          type="file"
          accept={accept}
          style={{ marginLeft: 12 }}
          onChange={onWebPick}
          disabled={loading}
        />
      )}
      {file && (
        <View
          style={{ marginLeft: 12, flexDirection: "row", alignItems: "center" }}
        >
          <CustomTextInput
            value={file.name || "Selected file"}
            editable={false}
            style={{ width: 160 }}
          />
          {onRemove && (
            <CustomButton
              text="Remove"
              onPress={onRemove}
              variant="danger"
              style={{ marginLeft: 8 }}
              disabled={loading}
            />
          )}
        </View>
      )}
    </View>
  );
}
