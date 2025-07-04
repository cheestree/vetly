import { AnimalCreate, Sex } from "@/api/animal/animal.input";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import size from "@/theme/size";
import * as ImagePicker from "expo-image-picker";
import { ImagePickerAsset } from "expo-image-picker";
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
import CustomDateInput from "../basic/custom/CustomDateInput";
import CustomImagePicker from "../basic/custom/CustomImagePicker";
import CustomTextInput from "../basic/custom/CustomTextInput";
import CustomToggleableButton from "../basic/custom/CustomToggleableButton";

type AnimalCreateFormProps = {
  onCreate: (
    createdAnimal: AnimalCreate,
    image?: ImagePickerAsset,
  ) => Promise<void>;
  loading?: boolean;
};

export default function AnimalCreateContent({
  onCreate,
  loading,
}: AnimalCreateFormProps) {
  const { styles } = useThemedStyles();
  const [formData, setFormData] = useState({
    name: "",
    microchip: "",
    sex: Sex.UNKNOWN,
    sterilized: false,
    species: "",
    birthDate: "",
    imageFile: null as ImagePickerAsset | null,
  });
  const [imagePreviewUrl, setImagePreviewUrl] = useState<string | null>(null);
  const { width } = useWindowDimensions();
  const isWeb = Platform.OS === "web";
  const isWideScreen = width >= 768;
  const isRowLayout = isWeb && isWideScreen;

  const handleInputChange = (
    field: keyof typeof formData,
    value: string | boolean | Sex,
  ) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const validateForm = () => {
    if (!formData.name.trim()) {
      Alert.alert("Validation Error", "Name is required");
      return false;
    }
    return true;
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
      setFormData((prev) => ({
        ...prev,
        imageFile: file,
      }));
      setImagePreviewUrl(URL.createObjectURL(file));
    }
  };

  const handleRemoveImage = () => {
    setFormData((prev) => ({
      ...prev,
      imageFile: null,
    }));
    setImagePreviewUrl(null);
  };

  const handleSave = async () => {
    if (!validateForm()) return;

    try {
      const updatedData = {
        name: formData.name.trim(),
        microchip: formData.microchip.trim() || undefined,
        sex: formData.sex,
        sterilized: formData.sterilized,
        species: formData.species.trim() || undefined,
        birthDate: formData.birthDate.trim() || undefined,
      };

      await onCreate(updatedData, formData.imageFile ?? undefined);
      Alert.alert("Success", "Animal information updated successfully");
    } catch (error) {
      Alert.alert("Error", "Failed to update animal information");
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
          handleWebFileChange={handleWebFileChange}
          handleNativeFileChange={handleSelectImage}
          handleRemoveImage={handleRemoveImage}
          loading={loading}
          imagePreviewUrl={imagePreviewUrl}
        />

        <View style={[styles.innerContainer && extras.formColumn]}>
          <CustomTextInput
            textLabel="Name"
            value={formData.name}
            onChangeText={(text) => handleInputChange("name", text)}
            placeholder="Enter animal name"
            editable={!loading}
          />

          <CustomTextInput
            textLabel="Microchip"
            value={formData.microchip}
            onChangeText={(text) => handleInputChange("microchip", text)}
            placeholder="Enter microchip number"
            editable={!loading}
          />

          <CustomTextInput
            textLabel="Species"
            value={formData.species}
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
                value={formData.sex}
                onChange={(value) => handleInputChange("sex", value)}
                stylePressable={styles.toggleButton}
              />
            </View>

            <View style={{ flex: 1 }}>
              <CustomToggleableButton
                list={[
                  { label: "Sterilized", icon: "check", value: "true" },
                  { label: "Not sterilized", icon: "times", value: "false" },
                ]}
                value={formData.sterilized}
                onChange={(value) => handleInputChange("sterilized", value)}
                stylePressable={styles.toggleButton}
              />
            </View>

            <View style={{ flex: 1 }}>
              <CustomDateInput
                value={formData.birthDate}
                onChange={(date) => handleInputChange("birthDate", date)}
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
