import { ClinicCreate } from "@/api/clinic/clinic.input";
import { useForm } from "@/hooks/useForm";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { ClinicCreateSchema } from "@/schemas/clinic.schema";
import size from "@/theme/size";
import { ScrollView } from "react-native";
import { Toast } from "toastify-react-native";
import CustomButton from "../basic/custom/CustomButton";
import CustomTextInput from "../basic/custom/CustomTextInput";

type ClinicCreateContentProps = {
  onCreate: (clinic: ClinicCreate) => Promise<void>;
  loading?: boolean;
};

type ClinicFormData = ClinicCreate;

const initialClinicFormData: ClinicFormData = {
  name: "",
  nif: "",
  address: "",
  lng: 0,
  lat: 0,
  phone: "",
  email: "",
  services: [],
  openingHours: [],
  ownerEmail: "",
};

export default function ClinicCreateContent({
  onCreate,
  loading = false,
}: ClinicCreateContentProps) {
  const { styles } = useThemedStyles();
  const { form, handleInputChange } = useForm<ClinicFormData>(
    initialClinicFormData,
  );

  const handleSave = async () => {
    const parseResult = ClinicCreateSchema.safeParse(form);

    if (!parseResult.success) {
      const firstError =
        parseResult.error.issues[0]?.message || "Validation error";
      Toast.error(firstError);
      return;
    }

    try {
      await onCreate(parseResult.data);
      Toast.success("Animal created successfully");
    } catch (e) {
      Toast.error("Failed to create animal");
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
        onChangeText={(text) => handleInputChange("name", text)}
        editable={!loading}
      />
      <CustomTextInput
        textLabel="NIF"
        value={form.nif}
        onChangeText={(text) => handleInputChange("nif", text)}
        editable={!loading}
      />
      <CustomTextInput
        textLabel="Address"
        value={form.address}
        onChangeText={(text) => handleInputChange("address", text)}
        editable={!loading}
      />
      <CustomTextInput
        textLabel="Longitude"
        value={form.lng.toString()}
        onChangeText={(text) => handleInputChange("lng", Number(text))}
        keyboardType="numeric"
        editable={!loading}
      />
      <CustomTextInput
        textLabel="Latitude"
        value={form.lat.toString()}
        onChangeText={(text) => handleInputChange("lat", Number(text))}
        keyboardType="numeric"
        editable={!loading}
      />
      <CustomTextInput
        textLabel="Phone"
        value={form.phone}
        onChangeText={(text) => handleInputChange("phone", text)}
        editable={!loading}
      />
      <CustomTextInput
        textLabel="Email"
        value={form.email}
        onChangeText={(text) => handleInputChange("email", text)}
        editable={!loading}
      />
      <CustomTextInput
        textLabel="Owners' email"
        value={form.ownerEmail}
        onChangeText={(text) => handleInputChange("ownerEmail", text)}
        editable={!loading}
      />

      {/* TODO: Add pickers for services and opening hours if needed */}

      <CustomButton
        onPress={handleSave}
        text={loading ? "Creating..." : "Create Clinic"}
        disabled={loading}
      />
    </ScrollView>
  );
}
