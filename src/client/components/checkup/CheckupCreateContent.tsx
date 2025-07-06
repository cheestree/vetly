import { CheckupCreate } from "@/api/checkup/checkup.input";
import { useAuth } from "@/hooks/useAuth";
import { useForm } from "@/hooks/useForm";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { CheckupCreateSchema } from "@/schemas/checkup.schema";
import size from "@/theme/size";
import { useLocalSearchParams } from "expo-router";
import { useEffect } from "react";
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
import CustomList from "../basic/custom/CustomList";
import CustomTextInput from "../basic/custom/CustomTextInput";

type CheckupCreateFormProps = {
  onCreate: (createdCheckup: CheckupCreate) => Promise<void>;
  loading?: boolean;
};

type CheckupFormData = CheckupCreate;

const initialCheckupFormData: CheckupFormData = {
  animalId: 0,
  veterinarianId: "",
  clinicId: 0,
  dateTime: "",
  title: "",
  description: "",
};

export default function CheckupCreateContent({
  onCreate,
  loading,
}: CheckupCreateFormProps) {
  const { styles } = useThemedStyles();
  const { information } = useAuth();
  const params = useLocalSearchParams();
  const passedAnimalId = params?.animalId ? Number(params.animalId) : undefined;
  const animalLocked = passedAnimalId !== undefined;

  const { form, handleInputChange } = useForm<CheckupFormData>(
    initialCheckupFormData,
  );
  const { width } = useWindowDimensions();
  const isWeb = Platform.OS === "web";
  const isWideScreen = width >= 768;
  const isRowLayout = isWeb && isWideScreen;

  const handleSave = async () => {
    const parseResult = CheckupCreateSchema.safeParse(form);

    if (!parseResult.success) {
      const firstError =
        parseResult.error.issues[0]?.message || "Validation error";
      Toast.error(firstError);
      return;
    }

    try {
      await onCreate(parseResult.data);
      Toast.success("Checkup created successfully");
    } catch (error) {
      Toast.error("Failed to create checkup");
    }
  };

  useEffect(() => {
    if (passedAnimalId !== undefined) {
      handleInputChange("animalId", passedAnimalId);
    }
  }, [passedAnimalId]);

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
            value={form.title}
            onChangeText={(text) => handleInputChange("title", text)}
            placeholder="Enter checkup title"
            editable={!loading}
            maxLength={64}
          />

          <CustomTextInput
            textLabel="Description"
            value={form.description}
            onChangeText={(text) => handleInputChange("description", text)}
            placeholder="Enter description"
            editable={!loading}
            multiline
            maxLength={256}
          />

          {information?.clinics.length !== 0 ? (
            <CustomList
              label="Clinic"
              list={
                information?.clinics?.map((membership) => ({
                  label: membership.clinic.name,
                  key: membership.clinic.id,
                  value: membership.clinic.id,
                })) ?? []
              }
              selectedItem={form.clinicId}
              onSelect={(text) => handleInputChange("clinicId", text)}
              disabled={!!loading}
            />
          ) : (
            <CustomTextInput
              textLabel="Clinic ID"
              value={form.clinicId.toString()}
              onChangeText={(text) =>
                handleInputChange("clinicId", Number(text))
              }
              placeholder="Enter clinic ID"
              editable={!loading}
            />
          )}

          <CustomTextInput
            textLabel="Animal ID"
            value={form.animalId.toString()}
            onChangeText={(text) => handleInputChange("animalId", Number(text))}
            placeholder="Enter animal ID"
            editable={!animalLocked && !loading}
          />

          <CustomDateInput
            value={form.dateTime}
            mode="dateTime"
            onChange={(date) => handleInputChange("dateTime", date)}
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
