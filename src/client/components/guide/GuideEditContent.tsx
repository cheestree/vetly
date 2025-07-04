import { GuideUpdate } from "@/api/guide/guide.input";
import { GuideInformation } from "@/api/guide/guide.output";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import size from "@/theme/size";
import { useEffect, useState } from "react";
import { Alert, ScrollView, View } from "react-native";
import CustomButton from "../basic/custom/CustomButton";
import CustomTextInput from "../basic/custom/CustomTextInput";

type GuideEditFormProps = {
  guide?: GuideInformation;
  onSave: (updatedGuide: Partial<GuideUpdate>) => Promise<void>;
  loading?: boolean;
};

export default function GuideEditContent({
  guide,
  onSave,
  loading = false,
}: GuideEditFormProps) {
  const { styles } = useThemedStyles();
  const [formData, setFormData] = useState({
    title: "",
    description: "",
    content: "",
    imageUrl: "",
  });

  useEffect(() => {
    if (guide) {
      setFormData({
        title: guide.title || "",
        description: guide.description || "",
        content: guide.content || "",
        imageUrl: guide.imageUrl || "",
      });
    }
  }, [guide]);

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
      const updatedData: Partial<GuideUpdate> = {
        title: formData.title.trim(),
        description: formData.description.trim() || undefined,
        content: formData.content.trim() || undefined,
        imageUrl: formData.imageUrl.trim() || undefined,
      };
      await onSave(updatedData);
      Alert.alert("Success", "Guide updated successfully");
    } catch (error) {
      Alert.alert("Error", "Failed to update guide");
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
          placeholder="Enter guide title"
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
        <CustomTextInput
          textLabel="Content"
          value={formData.content}
          onChangeText={(text) => handleInputChange("content", text)}
          placeholder="Enter content"
          editable={!loading}
          multiline
        />
        <CustomTextInput
          textLabel="Image URL"
          value={formData.imageUrl}
          onChangeText={(text) => handleInputChange("imageUrl", text)}
          placeholder="Enter image URL"
          editable={!loading}
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
