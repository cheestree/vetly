import { CheckupCreate } from "@/api/checkup/checkup.input";
import { useAuth } from "@/hooks/useAuth";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import size from "@/theme/size";
import { useLocalSearchParams } from "expo-router";
import { useState } from "react";
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
import CustomList from "../basic/custom/CustomList";
import CustomTextInput from "../basic/custom/CustomTextInput";

interface CheckupCreateFormProps {
  onCreate: (createdCheckup: CheckupCreate) => Promise<void>;
  loading?: boolean;
}

export default function CheckupCreateContent({
  onCreate,
  loading,
}: CheckupCreateFormProps) {
  const { styles } = useThemedStyles();
  const { information } = useAuth();
  const params = useLocalSearchParams();
  const passedAnimalId = params?.animalId ? Number(params.animalId) : undefined;
  const [formData, setFormData] = useState({
    title: "",
    description: "",
    date: "",
    animalId: passedAnimalId ?? 0,
    clinicId: information?.clinics?.[0]?.clinic.id ?? 0,
    veterinarianId: "",
    files: [],
  });
  const { width } = useWindowDimensions();
  const isWeb = Platform.OS === "web";
  const isWideScreen = width >= 768;
  const isRowLayout = isWeb && isWideScreen;
  const animalLocked = passedAnimalId !== undefined;

  const handleInputChange = (field: keyof typeof formData, value: string) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const validateForm = () => {
    if (!formData.title.trim()) {
      Alert.alert("Validation Error", "Title is required");
      return false;
    }
    if (formData.title.length > 64) {
      Alert.alert("Validation Error", "Title must be at most 64 characters");
      return false;
    }
    if (formData.description.length > 256) {
      Alert.alert(
        "Validation Error",
        "Description must be at most 256 characters",
      );
      return false;
    }
    if (!formData.date.trim()) {
      Alert.alert("Validation Error", "Date is required");
      return false;
    }
    return true;
  };

  const handleSave = async () => {
    if (!validateForm()) return;

    try {
      const checkupData: CheckupCreate = {
        title: formData.title.trim(),
        description: formData.description.trim() || "",
        dateTime: formData.date.trim(),
        veterinarianId: information?.publicId,
        clinicId: formData.clinicId,
        animalId: formData.animalId,
        files: [],
      };

      await onCreate(checkupData);
      Alert.alert("Success", "Checkup created successfully");
    } catch (error) {
      Alert.alert("Error", "Failed to create checkup");
    }
  };

  const handleClinicSelect = (clinicId: number) => {
    setFormData((prev) => ({
      ...prev,
      clinicId: clinicId,
    }));
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
        <View style={[styles.innerContainer && extras.formColumn]}>
          <CustomTextInput
            textLabel="Title"
            value={formData.title}
            onChangeText={(text) => handleInputChange("title", text)}
            placeholder="Enter checkup title"
            editable={!loading}
            maxLength={64}
          />

          <CustomTextInput
            textLabel="Description"
            value={formData.description}
            onChangeText={(text) => handleInputChange("description", text)}
            placeholder="Enter description"
            editable={!loading}
            multiline
            maxLength={256}
          />

          {information?.clinics.length !== 0 ? (
            <CustomList
              list={
                information?.clinics?.map((membership) => ({
                  label: membership.clinic.name,
                  key: membership.clinic.id,
                  value: membership.clinic.id,
                })) ?? []
              }
              selectedItem={formData.clinicId}
              onSelect={handleClinicSelect}
              disabled={!!loading}
            />
          ) : (
            <CustomTextInput
              textLabel="Clinic ID"
              value={formData.clinicId.toString()}
              onChangeText={(text) => handleInputChange("clinicId", text)}
              placeholder="Enter clinic ID"
              editable={!loading}
            />
          )}

          <CustomTextInput
            textLabel="Animal ID"
            value={formData.animalId.toString()}
            onChangeText={(text) =>
              setFormData((prev) => ({ ...prev, animalId: Number(text) }))
            }
            placeholder="Enter animal ID"
            editable={!animalLocked && !loading}
          />

          <CustomDateInput
            value={formData.date}
            onChange={(date) => handleInputChange("date", date)}
          />

          <CustomButton
            onPress={handleSave}
            text={loading ? "Saving..." : "Create Checkup"}
            disabled={!!loading}
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
