import { GuideCreate } from "@/api/guide/guide.input";
import { useFilePicker } from "@/hooks/useFilePicker";
import { useForm } from "@/hooks/useForm";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { GuideCreateSchema } from "@/schemas/guide.schema";
import size from "@/theme/size";
import * as DocumentPicker from "expo-document-picker";
import * as ImagePicker from "expo-image-picker";
import {
  Platform,
  ScrollView,
  StyleSheet,
  useWindowDimensions,
  View,
} from "react-native";
import { Toast } from "toastify-react-native";
import CustomButton from "../basic/custom/CustomButton";
import CustomFilePicker from "../basic/custom/CustomFilePicker";
import CustomImagePicker from "../basic/custom/CustomImagePicker";
import CustomTextInput from "../basic/custom/CustomTextInput";

type GuideCreateContentProps = {
  onCreate: (
    createdGuide: GuideCreate,
    file: DocumentPicker.DocumentPickerAsset | File | null,
    image: ImagePicker.ImagePickerAsset | File | null,
  ) => Promise<void>;
  loading?: boolean;
};

type GuideFormData = GuideCreate & {
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

export default function GuideCreateContent({
  onCreate,
  loading = false,
}: GuideCreateContentProps) {
  const { styles } = useThemedStyles();
  const { form, handleInputChange } =
    useForm<GuideFormData>(initialGuideFormData);
  const {
    pickFile,
    pickImage,
    handleWebImageChange,
    previewUrl: imagePreviewUrl,
    clearPreview: handleRemoveImage,
  } = useFilePicker();
  const { width } = useWindowDimensions();
  const isWeb = Platform.OS === "web";
  const isWideScreen = width >= 768;
  const isRowLayout = isWeb && isWideScreen;

  const handleSubmit = async () => {
    const parseResult = GuideCreateSchema.safeParse(form);

    if (!parseResult.success) {
      const firstError =
        parseResult.error.issues[0]?.message || "Validation error";
      Toast.error(firstError);
      return;
    }

    try {
      await onCreate(parseResult.data, form.file, form.image);
      Toast.success("Guide created successfully");
    } catch (error) {
      Toast.error("Failed to create guide");
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
          imagePreviewUrl={imagePreviewUrl}
        />

        <View style={[styles.innerContainer && extras.formColumn]}>
          <CustomTextInput
            textLabel="Title"
            value={form.title}
            onChangeText={(text) => handleInputChange("title", text)}
            editable={!loading}
          />
          <CustomTextInput
            textLabel="Description"
            value={form.description}
            onChangeText={(text) => handleInputChange("description", text)}
            editable={!loading}
          />
          <CustomTextInput
            textLabel="Content"
            value={form.content}
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
              file={form.file}
              onPick={() => pickFile((file) => handleInputChange("file", file))}
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
