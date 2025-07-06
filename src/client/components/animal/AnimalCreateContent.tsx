import { AnimalCreate, Sex } from "@/api/animal/animal.input";
import { useFilePicker } from "@/hooks/useFilePicker";
import { useForm } from "@/hooks/useForm";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { dropMilliseconds } from "@/lib/utils";
import { AnimalCreateSchema } from "@/schemas/animal.schema";
import size from "@/theme/size";
import { ImagePickerAsset } from "expo-image-picker";
import {
  Platform,
  ScrollView,
  StyleSheet,
  useWindowDimensions,
  View,
} from "react-native";
import { Toast } from "toastify-react-native";
import CustomButton from "../basic/custom/CustomButton";
import CustomDateInput from "../basic/custom/CustomDateInput";
import CustomImagePicker from "../basic/custom/CustomImagePicker";
import CustomTextInput from "../basic/custom/CustomTextInput";
import CustomToggleableButton from "../basic/custom/CustomToggleableButton";

type AnimalCreateFormProps = {
  onCreate: (
    createdAnimal: AnimalCreate,
    image: ImagePickerAsset | File | null,
  ) => Promise<void>;
  loading?: boolean;
};

type AnimalFormData = AnimalCreate & {
  image: ImagePickerAsset | File | null;
};

const initialAnimalFormData: AnimalFormData = {
  name: "",
  microchip: "",
  sex: Sex.UNKNOWN,
  sterilized: false,
  species: "",
  birthDate: "",
  ownerEmail: "",
  image: null,
};

export default function AnimalCreateContent({
  onCreate,
  loading = false,
}: AnimalCreateFormProps) {
  const { styles } = useThemedStyles();
  const { form, handleInputChange } = useForm<AnimalFormData>(
    initialAnimalFormData,
  );
  const {
    pickImage,
    handleWebImageChange,
    previewUrl: imagePreviewUrl,
    clearPreview: handleRemoveImage,
  } = useFilePicker();
  const { width } = useWindowDimensions();
  const isWeb = Platform.OS === "web";
  const isWideScreen = width >= 768;
  const isRowLayout = isWeb && isWideScreen;

  const handleSave = async () => {
    const parseResult = AnimalCreateSchema.safeParse(form);

    if (!parseResult.success) {
      const firstError =
        parseResult.error.issues[0]?.message || "Validation error";
      Toast.error(firstError);
      return;
    }

    try {
      await onCreate(parseResult.data, form.image);
      Toast.success("Animal created successfully");
    } catch (error) {
      Toast.error("Failed to create animal");
    }
  };

  return (
    <ScrollView
      style={[styles.innerContainer]}
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
            textLabel="Name"
            value={form.name}
            onChangeText={(text) => handleInputChange("name", text)}
            placeholder="Enter animal name"
            editable={!loading}
          />

          <CustomTextInput
            textLabel="Microchip"
            value={form.microchip}
            onChangeText={(text) => handleInputChange("microchip", text)}
            placeholder="Enter microchip number"
            editable={!loading}
          />

          <CustomTextInput
            textLabel="Species"
            value={form.species}
            onChangeText={(text) => handleInputChange("species", text)}
            placeholder="Enter species (e.g., Dog, Cat)"
            editable={!loading}
          />

          <View
            style={{
              flexDirection: isRowLayout ? "row" : "column",
              flex: 1,
              justifyContent: "space-between",
              gap: size.gap.md,
            }}
          >
            <View style={{ flex: 1 }}>
              <CustomToggleableButton
                list={[
                  { label: "Male", icon: "mars", value: Sex.MALE },
                  { label: "Female", icon: "venus", value: Sex.FEMALE },
                  { label: "Unknown", icon: "question", value: Sex.UNKNOWN },
                ]}
                value={form.sex}
                onChange={(value) => handleInputChange("sex", value)}
                stylePressable={styles.toggleButton}
              />
            </View>

            <View style={{ flex: 1 }}>
              <CustomToggleableButton
                list={[
                  { label: "Sterilized", icon: "check", value: true },
                  { label: "Not sterilized", icon: "times", value: false },
                ]}
                value={form.sterilized}
                onChange={(value) => handleInputChange("sterilized", value)}
                stylePressable={styles.toggleButton}
              />
            </View>

            <View style={{ flex: 1 }}>
              <CustomDateInput
                mode="dateTime"
                value={form.birthDate}
                onChange={(date) =>
                  handleInputChange("birthDate", dropMilliseconds(date))
                }
              />
            </View>
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

  formColumn: {
    flex: 2,
    width: "100%",
    gap: size.gap.md,
    padding: size.padding.xl,
  },
});
