import { ClinicCreate } from "@/api/clinic/clinic.input";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import size from "@/theme/size";
import { useState } from "react";
import { Alert, ScrollView } from "react-native";
import CustomButton from "../basic/custom/CustomButton";
import CustomTextInput from "../basic/custom/CustomTextInput";

type ClinicCreateContentProps = {
  onCreate: (clinic: ClinicCreate) => Promise<void>;
  loading?: boolean;
};

export default function ClinicCreateContent({
  onCreate,
  loading = false,
}: ClinicCreateContentProps) {
  const { styles } = useThemedStyles();
  const [form, setForm] = useState<ClinicCreate>({
    name: "",
    nif: "",
    address: "",
    lng: 0,
    lat: 0,
    phone: "",
    email: "",
    services: [],
    openingHours: [],
    ownerId: undefined,
  });

  const handleChange = <K extends keyof ClinicCreate>(
    key: K,
    value: ClinicCreate[K],
  ) => {
    setForm((prev) => ({ ...prev, [key]: value }));
  };

  const validateForm = () => {
    if (!form.name.trim() || !form.nif.trim() || !form.address.trim()) {
      Alert.alert("Validation Error", "Name, NIF, and Address are required.");
      return false;
    }
    return true;
  };

  const handleSubmit = async () => {
    if (!validateForm()) return;
    try {
      await onCreate(form);
      Alert.alert("Success", "Clinic created successfully");
    } catch (error) {
      Alert.alert("Error", "Failed to create clinic");
    }
  };

  return (
    <ScrollView
      style={styles.innerContainer}
      contentContainerStyle={{ padding: size.padding.xl }}
    >
      <CustomTextInput
        textLabel="Clinic Name"
        value={form.name}
        onChangeText={(text) => handleChange("name", text)}
        editable={!loading}
      />
      <CustomTextInput
        textLabel="NIF"
        value={form.nif}
        onChangeText={(text) => handleChange("nif", text)}
        editable={!loading}
      />
      <CustomTextInput
        textLabel="Address"
        value={form.address}
        onChangeText={(text) => handleChange("address", text)}
        editable={!loading}
      />
      <CustomTextInput
        textLabel="Longitude"
        value={form.lng.toString()}
        onChangeText={(text) => handleChange("lng", Number(text))}
        keyboardType="numeric"
        editable={!loading}
      />
      <CustomTextInput
        textLabel="Latitude"
        value={form.lat.toString()}
        onChangeText={(text) => handleChange("lat", Number(text))}
        keyboardType="numeric"
        editable={!loading}
      />
      <CustomTextInput
        textLabel="Phone"
        value={form.phone}
        onChangeText={(text) => handleChange("phone", text)}
        editable={!loading}
      />
      <CustomTextInput
        textLabel="Email"
        value={form.email}
        onChangeText={(text) => handleChange("email", text)}
        editable={!loading}
      />

      {/* TODO: Add pickers for services and opening hours if needed */}

      <CustomButton
        onPress={handleSubmit}
        text={loading ? "Creating..." : "Create Clinic"}
        disabled={loading}
      />
    </ScrollView>
  );
}
