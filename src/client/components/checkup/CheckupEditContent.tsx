import { CheckupUpdate } from "@/api/checkup/checkup.input";
import { CheckupInformation } from "@/api/checkup/checkup.output";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import size from "@/theme/size";
import { useEffect, useState } from "react";
import { Alert, ScrollView, View } from "react-native";
import CustomButton from "../basic/custom/CustomButton";
import CustomDateInput from "../basic/custom/CustomDateInput";
import CustomTextInput from "../basic/custom/CustomTextInput";

interface CheckupEditFormProps {
  checkup?: CheckupInformation;
  onSave: (updatedCheckup: Partial<CheckupUpdate>) => Promise<void>;
  loading?: boolean;
}

export default function CheckupEditContent({
  checkup,
  onSave,
  loading = false,
}: CheckupEditFormProps) {
  const { styles } = useThemedStyles();
  const [formData, setFormData] = useState({
    title: "",
    description: "",
    dateTime: "",
  });

  useEffect(() => {
    if (checkup) {
      setFormData({
        title: checkup.title || "",
        description: checkup.description || "",
        dateTime: checkup.dateTime || "",
      });
    }
  }, [checkup]);

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
    return true;
  };

  const handleSave = async () => {
    if (!validateForm()) return;

    try {
      const updatedData: Partial<CheckupUpdate> = {
        title: formData.title.trim(),
        description: formData.description.trim() || undefined,
        dateTime: formData.dateTime.trim() || undefined,
      };
      await onSave(updatedData);
      Alert.alert("Success", "Checkup updated successfully");
    } catch (error) {
      Alert.alert("Error", "Failed to update checkup");
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
          value={formData.title}
          onChangeText={(text) => handleInputChange("title", text)}
          placeholder="Enter checkup title"
          editable={!loading}
        />
        <CustomTextInput
          textLabel="Description"
          value={formData.description}
          onChangeText={(text) => handleInputChange("description", text)}
          placeholder="Enter description"
          editable={!loading}
          multiline
        />
        <CustomDateInput
          value={formData.dateTime}
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
