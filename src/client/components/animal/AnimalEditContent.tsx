import { AnimalUpdate, Sex } from "@/api/animal/animal.input";
import { AnimalInformation } from "@/api/animal/animal.output";
import * as ImagePicker from "expo-image-picker";
import { ImagePickerAsset } from "expo-image-picker";
import { useEffect, useState } from "react";
import {
  Alert,
  Button,
  Image,
  Platform,
  ScrollView,
  StyleSheet,
  Switch,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";

interface AnimalEditFormProps {
  animal?: AnimalInformation;
  onSave: (
    updatedAnimal: Partial<AnimalUpdate>,
    image?: ImagePickerAsset | File,
  ) => Promise<void>;
  loading?: boolean;
}

export default function AnimalEditForm({
  animal,
  onSave,
  loading = false,
}: AnimalEditFormProps) {
  const isWeb = Platform.OS === "web";
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

  useEffect(() => {
    if (animal) {
      setFormData({
        name: animal.name || "",
        microchip: animal.microchip || "",
        sex: animal.sex || Sex.UNKNOWN,
        sterilized: animal.sterilized || false,
        species: animal.species || "",
        birthDate: animal.birthDate || "",
        imageFile: null,
      });

      setImagePreviewUrl(animal.imageUrl || null);
    }
  }, [animal]);

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

  const handleWebFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
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

      await onSave(updatedData, formData.imageFile ?? undefined);
      Alert.alert("Success", "Animal information updated successfully");
    } catch (error) {
      Alert.alert("Error", "Failed to update animal information");
    }
  };

  const renderSexPicker = () => (
    <View style={styles.pickerContainer}>
      <Text style={styles.label}>Sex</Text>
      <View style={styles.sexButtons}>
        {Object.values(Sex).map((sex) => (
          <TouchableOpacity
            key={sex}
            style={[
              styles.sexButton,
              formData.sex === sex && styles.sexButtonActive,
            ]}
            onPress={() => handleInputChange("sex", sex)}
          >
            <Text
              style={[
                styles.sexButtonText,
                formData.sex === sex && styles.sexButtonTextActive,
              ]}
            >
              {sex}
            </Text>
          </TouchableOpacity>
        ))}
      </View>
    </View>
  );

  return (
    <ScrollView style={styles.container} showsVerticalScrollIndicator={false}>
      <View style={styles.form}>
        {/* Name Field */}
        <View style={styles.fieldContainer}>
          <Text style={styles.label}>Name *</Text>
          <TextInput
            style={styles.input}
            value={formData.name}
            onChangeText={(text) => handleInputChange("name", text)}
            placeholder="Enter animal name"
            editable={!loading}
          />
        </View>

        {/* Microchip Field */}
        <View style={styles.fieldContainer}>
          <Text style={styles.label}>Microchip</Text>
          <TextInput
            style={styles.input}
            value={formData.microchip}
            onChangeText={(text) => handleInputChange("microchip", text)}
            placeholder="Enter microchip number"
            editable={!loading}
          />
        </View>

        {/* Species Field */}
        <View style={styles.fieldContainer}>
          <Text style={styles.label}>Species</Text>
          <TextInput
            style={styles.input}
            value={formData.species}
            onChangeText={(text) => handleInputChange("species", text)}
            placeholder="Enter species (e.g., Dog, Cat)"
            editable={!loading}
          />
        </View>

        {/* Sex Picker */}
        {renderSexPicker()}

        {/* Sterilized Switch */}
        <View style={styles.switchContainer}>
          <Text style={styles.label}>Sterilized</Text>
          <Switch
            value={formData.sterilized}
            onValueChange={(value) => handleInputChange("sterilized", value)}
            disabled={loading}
          />
        </View>

        {/* Birth Date Field */}
        <View style={styles.fieldContainer}>
          <Text style={styles.label}>Birth Date</Text>
          <TextInput
            style={styles.input}
            value={formData.birthDate}
            onChangeText={(text) => handleInputChange("birthDate", text)}
            placeholder="YYYY-MM-DD"
            editable={!loading}
          />
        </View>

        {/* Image Picker */}
        <View style={styles.fieldContainer}>
          <Text style={styles.label}>Image</Text>

          {imagePreviewUrl && (
            <Image
              source={{ uri: imagePreviewUrl }}
              style={{ width: 100, height: 100, marginBottom: 8 }}
            />
          )}

          <View style={{ flexDirection: "row", gap: 10 }}>
            {isWeb ? (
              <input
                title="Upload Image"
                type="file"
                accept="image/*"
                onChange={handleWebFileChange}
              />
            ) : (
              <Button
                title="Select Image"
                onPress={handleSelectImage}
                disabled={loading}
              />
            )}
            {imagePreviewUrl && (
              <Button
                title="Remove"
                onPress={handleRemoveImage}
                color="red"
                disabled={loading}
              />
            )}
          </View>
        </View>

        {/* Save Button */}
        <TouchableOpacity
          style={[styles.saveButton, loading && styles.saveButtonDisabled]}
          onPress={handleSave}
          disabled={loading}
        >
          <Text style={styles.saveButtonText}>
            {loading ? "Saving..." : "Save Changes"}
          </Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
  },
  form: {
    padding: 16,
  },
  fieldContainer: {
    marginBottom: 20,
  },
  label: {
    fontSize: 16,
    fontWeight: "600",
    marginBottom: 8,
    color: "#333",
  },
  input: {
    borderWidth: 1,
    borderColor: "#ddd",
    borderRadius: 8,
    padding: 12,
    fontSize: 16,
    backgroundColor: "#f9f9f9",
  },
  pickerContainer: {
    marginBottom: 20,
  },
  sexButtons: {
    flexDirection: "row",
    gap: 10,
  },
  sexButton: {
    flex: 1,
    paddingVertical: 12,
    paddingHorizontal: 16,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: "#ddd",
    backgroundColor: "#f9f9f9",
    alignItems: "center",
  },
  sexButtonActive: {
    backgroundColor: "#007AFF",
    borderColor: "#007AFF",
  },
  sexButtonText: {
    fontSize: 14,
    fontWeight: "500",
    color: "#333",
  },
  sexButtonTextActive: {
    color: "#fff",
  },
  switchContainer: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 20,
    paddingVertical: 8,
  },
  saveButton: {
    backgroundColor: "#007AFF",
    paddingVertical: 16,
    borderRadius: 8,
    alignItems: "center",
    marginTop: 20,
  },
  saveButtonDisabled: {
    backgroundColor: "#ccc",
  },
  saveButtonText: {
    color: "#fff",
    fontSize: 18,
    fontWeight: "600",
  },
});
