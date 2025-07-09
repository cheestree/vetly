import { GuideUpdate } from "@/api/guide/guide.input";
import { GuideInformation } from "@/api/guide/guide.output";
import { useFilePicker } from "@/hooks/useFilePicker";
import { useForm } from "@/hooks/useForm";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { GuideUpdateSchema } from "@/schemas/guide.schema";
import size from "@/theme/size";
import * as DocumentPicker from "expo-document-picker";
import * as ImagePicker from "expo-image-picker";
import { useEffect, useState } from "react";
import {
  Linking,
  Platform,
  ScrollView,
  StyleSheet,
  Text,
  useWindowDimensions,
  View,
} from "react-native";
import { Toast } from "toastify-react-native";
import CustomButton from "../basic/custom/CustomButton";
import CustomFilePicker from "../basic/custom/CustomFilePicker";
import CustomImagePicker from "../basic/custom/CustomImagePicker";
import CustomTextInput from "../basic/custom/CustomTextInput";

type GuideUpdateFormProps = {
  guide?: GuideInformation;
  onSave: (
    updatedGuide: GuideUpdate,
    file: DocumentPicker.DocumentPickerAsset | File | null,
    image: ImagePicker.ImagePickerAsset | File | null,
  ) => Promise<void>;
  loading?: boolean;
};

type GuideFormData = GuideUpdate & {
  file: DocumentPicker.DocumentPickerAsset | File | null;
  image: ImagePicker.ImagePickerAsset | File | null;
};

const initialGuideFormData: GuideFormData = {
  title: "",
  description: "",
  content: "",
  file: null,
  image: null,
};

export default function GuideEditContent({
  guide,
  onSave,
  loading = false,
}: GuideUpdateFormProps) {
  const { styles } = useThemedStyles();
  const { form, setForm, handleInputChange } =
    useForm<GuideFormData>(initialGuideFormData);
  const {
    pickFile,
    pickImage,
    previewUrl,
    setPreviewUrl,
    handleWebImageChange,
    clearPreview: handleRemoveImage,
  } = useFilePicker();
  const [filePreviewUrl, setFilePreviewUrl] = useState<string | null>(null);
  const { width } = useWindowDimensions();
  const isWeb = Platform.OS === "web";
  const isWideScreen = width >= 768;
  const isRowLayout = isWeb && isWideScreen;

  useEffect(() => {
    if (guide) {
      setForm({
        title: guide.title,
        description: guide.description,
        content: guide.content,
        file: null,
        image: null,
      });
      setPreviewUrl(guide.imageUrl || null);
      setFilePreviewUrl(guide.fileUrl || null);
    }
  }, [guide]);

  const handleSave = async () => {
    const parseResult = GuideUpdateSchema.safeParse(form);

    if (!parseResult.success) {
      const firstError =
        parseResult.error.issues[0]?.message || "Validation error";
      Toast.error(firstError);
      return;
    }

    try {
      await onSave(parseResult.data, form.file, form.image);
      Toast.success("Guide updated successfully");
    } catch (e) {
      Toast.error("Failed to update guide");
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
          handleWebFileChange={(e) =>
            handleWebImageChange(e, (file) => handleInputChange("image", file))
          }
          handleNativeFileChange={() =>
            pickImage((image) => handleInputChange("image", image))
          }
          handleRemoveImage={handleRemoveImage}
          loading={loading}
          imagePreviewUrl={previewUrl}
        />
        <View style={[styles.innerContainer && extras.formColumn]}>
          <CustomTextInput
            textLabel="Title"
            value={form.title}
            onChangeText={(text) => handleInputChange("title", text)}
            placeholder="Enter guide title"
            editable={!loading}
          />
          <CustomTextInput
            textLabel="Description"
            value={form.description}
            onChangeText={(text) => handleInputChange("description", text)}
            placeholder="Enter description"
            editable={!loading}
            multiline
          />
          <CustomTextInput
            textLabel="Content"
            value={form.content}
            onChangeText={(text) => handleInputChange("content", text)}
            placeholder="Enter content"
            editable={!loading}
            multiline
          />
          <View
            style={{
              flexDirection: "row",
              alignItems: "center",
              marginBottom: 16,
            }}
          >
            <CustomFilePicker
              file={form.file}
              onPick={() =>
                pickFile((file) => {
                  handleInputChange("file", file);
                  setFilePreviewUrl(null);
                })
              }
              onWebPick={(e) => {
                const file = e.target.files?.[0];
                if (file) {
                  handleInputChange("file", file);
                  setFilePreviewUrl(null);
                }
              }}
              onRemove={() => {
                handleInputChange("file", null);
                setFilePreviewUrl(null);
              }}
              label="File"
              accept="application/pdf"
              loading={loading}
            />
            {filePreviewUrl && !form.file && (
              <Text style={{ marginTop: 8 }}>
                Current file:{" "}
                <Text
                  style={{ color: "blue", textDecorationLine: "underline" }}
                  onPress={() => {
                    Linking.openURL(filePreviewUrl);
                  }}
                >
                  {filePreviewUrl.split("/").pop()}
                </Text>
              </Text>
            )}
          </View>
          <CustomButton
            onPress={handleSave}
            text={loading ? "Saving..." : "Save Changes"}
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
