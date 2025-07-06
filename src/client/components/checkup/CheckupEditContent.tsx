import { CheckupUpdate } from "@/api/checkup/checkup.input";
import { CheckupInformation } from "@/api/checkup/checkup.output";
import { useForm } from "@/hooks/useForm";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { CheckupUpdateSchema } from "@/schemas/checkup.schema";
import size from "@/theme/size";
import { useEffect } from "react";
import { ScrollView, View } from "react-native";
import { Toast } from "toastify-react-native";
import CustomButton from "../basic/custom/CustomButton";
import CustomDateInput from "../basic/custom/CustomDateInput";
import CustomTextInput from "../basic/custom/CustomTextInput";

type CheckupEditFormProps = {
  checkup?: CheckupInformation;
  onSave: (updatedCheckup: CheckupUpdate) => Promise<void>;
  loading?: boolean;
};

type CheckupFormData = CheckupUpdate;

const initialCheckupFormData: CheckupFormData = {
  title: "",
  dateTime: "",
  description: "",
};

export default function CheckupEditContent({
  checkup,
  onSave,
  loading = false,
}: CheckupEditFormProps) {
  const { styles } = useThemedStyles();
  const { form, setForm, handleInputChange } = useForm<CheckupFormData>(
    initialCheckupFormData,
  );

  useEffect(() => {
    if (checkup) {
      setForm({
        title: checkup.title,
        description: checkup.description,
        dateTime: checkup.dateTime,
      });
    }
  }, [checkup]);

  const handleSave = async () => {
    const parseResult = CheckupUpdateSchema.safeParse(form);

    if (!parseResult.success) {
      const firstError =
        parseResult.error.issues[0]?.message || "Validation error";
      Toast.error(firstError);
      return;
    }

    try {
      await onSave(parseResult.data);
      Toast.success("Checkup updated successfully");
    } catch (error) {
      Toast.error("Failed to update checkup");
    }
  };

  return (
    <ScrollView
      style={[styles.innerContainer]}
      showsVerticalScrollIndicator={false}
    >
      <View style={{ gap: size.gap.md, padding: size.padding.xl }}>
        <CustomTextInput
          textLabel="Title"
          value={form.title}
          onChangeText={(text) => handleInputChange("title", text)}
          placeholder="Enter checkup title"
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
        <CustomDateInput
          value={form.dateTime}
          onChange={(date) => handleInputChange("dateTime", date)}
        />
        <CustomButton
          onPress={handleSave}
          text={loading ? "Saving..." : "Save Changes"}
          disabled={loading}
        />
      </View>
    </ScrollView>
  );
}
