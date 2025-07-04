import { GuideCreate } from "@/api/guide/guide.input";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import size from "@/theme/size";
import * as DocumentPicker from "expo-document-picker";
import * as ImagePicker from "expo-image-picker";
import { ChangeEvent, useState } from "react";
import {
  Alert,
  Platform,
  ScrollView,
  StyleSheet,
  useWindowDimensions,
  View,
} from "react-native";
import CustomButton from "../basic/custom/CustomButton";
import CustomFilePicker from "../basic/custom/CustomFilePicker";
import CustomImagePicker from "../basic/custom/CustomImagePicker";
import CustomTextInput from "../basic/custom/CustomTextInput";

type GuideCreateContentProps = {
  onCreate: (
    createdGuide: GuideCreate,
    file?: DocumentPicker.DocumentPickerAsset | File,
    image?: ImagePicker.ImagePickerAsset | File,
  ) => Promise<void>;
  loading?: boolean;
};

export default function GuideCreateContent({
  onCreate,
  loading = false,
}: GuideCreateContentProps) {
  const { styles } = useThemedStyles();
  const [formData, setFormData] = useState({
    title: "",
    description: "",
    content: "",
    file: null as DocumentPicker.DocumentPickerAsset | null,
    imageFile: null as ImagePicker.ImagePickerAsset | File | null,
  });
  const [imagePreviewUrl, setImagePreviewUrl] = useState<string | null>(null);
  const { width } = useWindowDimensions();
  const isWeb = Platform.OS === "web";
  const isWideScreen = width >= 768;
  const isRowLayout = isWeb && isWideScreen;

  const handleInputChange = (
    field: keyof typeof formData,
    value:
      | string
      | DocumentPicker.DocumentPickerAsset
      | ImagePicker.ImagePickerAsset
      | File
      | null,
  ) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const handlePickFile = async () => {
    try {
      const result = await DocumentPicker.getDocumentAsync({
        type: "application/pdf",
        multiple: false,
        copyToCacheDirectory: true,
      });

      if (!result.canceled && result.assets && result.assets[0]) {
        handleInputChange("file", result.assets[0]);
      }
    } catch (error) {
      Alert.alert("Error", "Failed to pick file");
    }
  };

  const handleSelectImage = async () => {
    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ["images"],
      allowsEditing: true,
      aspect: [4, 3],
      quality: 1,
      selectionLimit: 1,
    });
    if (!result.canceled) {
      const file = result.assets[0];

      setFormData((prev) => ({
        ...prev,
        imageFile: file,
      }));

      setImagePreviewUrl(file.uri);
    }
  };

  const handleWebFileChange = (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      handleInputChange("imageFile", file);
      setImagePreviewUrl(URL.createObjectURL(file));
    }
  };

  const handleRemoveImage = () => {
    handleInputChange("imageFile", null);
    setImagePreviewUrl(null);
  };

  const validateForm = () => {
    if (!formData.title.trim() || !formData.content.trim()) {
      Alert.alert("Validation Error", "Title and content are required.");
      return false;
    }
    return true;
  };

  const handleSubmit = async () => {
    if (!validateForm()) return;
    try {
      const request: GuideCreate = {
        title: formData.title,
        description: formData.description,
        content: formData.content,
      };
      await onCreate(
        request,
        formData.file ?? undefined,
        formData.imageFile ?? undefined,
      );
      Alert.alert("Success", "Guide created successfully");
    } catch (error) {
      Alert.alert("Error", "Failed to create guide");
    }
  };

  return (
    <ScrollView
      style={styles.innerContainer}
      contentContainerStyle={{ padding: size.padding.xl }}
      showsVerticalScrollIndicator={false}
    >
      <View
        style={[
          extras.rowContainer,
          {
            flexDirection: isRowLayout ? "row" : "column",
            alignSelf: isRowLayout ? "center" : "stretch",
          },
        ]}
      >
        <CustomImagePicker
          handleWebFileChange={handleWebFileChange}
          handleNativeFileChange={handleSelectImage}
          handleRemoveImage={handleRemoveImage}
          loading={loading}
          imagePreviewUrl={imagePreviewUrl}
        />

        {/* Form Fields */}
        <View style={[styles.innerContainer && extras.formColumn]}>
          <CustomTextInput
            textLabel="Title"
            value={formData.title}
            onChangeText={(text) => handleInputChange("title", text)}
            editable={!loading}
          />
          <CustomTextInput
            textLabel="Description"
            value={formData.description}
            onChangeText={(text) => handleInputChange("description", text)}
            editable={!loading}
          />
          <CustomTextInput
            textLabel="Content"
            value={formData.content}
            onChangeText={(text) => handleInputChange("content", text)}
            editable={!loading}
            multiline
            style={{ minHeight: 120 }}
          />
          <View
            style={{
              flexDirection: "row",
              alignItems: "center",
              marginBottom: 16,
            }}
          >
            <CustomFilePicker
              file={formData.file}
              onPick={handlePickFile}
              onWebPick={(e) => {
                const file = e.target.files?.[0];
                if (file) handleInputChange("file", file);
              }}
              onRemove={() => handleInputChange("file", null)}
              label="File"
              accept="application/pdf"
              loading={loading}
            />
          </View>
          <CustomButton
            onPress={handleSubmit}
            text={loading ? "Creating..." : "Create Guide"}
            disabled={loading}
          />
        </View>
      </View>
    </ScrollView>
  );
}

const extras = StyleSheet.create({
  rowContainer: {
    justifyContent: "space-between",
    alignItems: "flex-start",
    gap: size.gap.lg,
    width: "100%",
  },
  imageColumn: {
    flex: 1,
    alignItems: "center",
    gap: size.gap.md,
    minWidth: 180,
    marginBottom: 24,
  },
  formColumn: {
    flex: 2,
    width: "100%",
    gap: size.gap.md,
    padding: size.padding.xl,
  },
});
