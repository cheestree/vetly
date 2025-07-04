import { ClinicCreate } from "@/api/clinic/clinic.input";
import { useEffect, useState } from "react";
import { View } from "react-native";
import CustomTextInput from "../../basic/custom/CustomTextInput";

type Props = {
  value: Partial<ClinicCreate>;
  onChange: (v: Partial<ClinicCreate>) => void;
  disabled?: boolean;
};

export default function ClinicCreateForm({ value, onChange, disabled }: Props) {
  const [form, setForm] = useState<Partial<ClinicCreate>>({
    name: value?.name ?? "",
    nif: value?.nif ?? "",
    address: value?.address ?? "",
    lng: value?.lng ?? undefined,
    lat: value?.lat ?? undefined,
    phone: value?.phone ?? "",
    email: value?.email ?? "",
    services: value?.services ?? [],
    openingHours: value?.openingHours ?? [],
    ownerId: value?.ownerId ?? undefined,
  });

  useEffect(() => {
    onChange(form);
  }, [form]);

  const handleChange = <K extends keyof ClinicCreate>(
    key: K,
    val: ClinicCreate[K],
  ) => {
    setForm((prev) => ({ ...prev, [key]: val }));
  };

  return (
    <View style={{ gap: 8 }}>
      <CustomTextInput
        textLabel="Clinic Name"
        value={form.name ?? ""}
        onChangeText={(text) => handleChange("name", text)}
        editable={!disabled}
      />
      <CustomTextInput
        textLabel="NIF"
        value={form.nif ?? ""}
        onChangeText={(text) => handleChange("nif", text)}
        editable={!disabled}
      />
      <CustomTextInput
        textLabel="Address"
        value={form.address ?? ""}
        onChangeText={(text) => handleChange("address", text)}
        editable={!disabled}
      />
      <CustomTextInput
        textLabel="Longitude"
        value={form.lng !== undefined ? String(form.lng) : ""}
        onChangeText={(text) =>
          handleChange("lng", text === "" ? 0 : Number(text))
        }
        editable={!disabled}
        keyboardType="numeric"
      />
      <CustomTextInput
        textLabel="Latitude"
        value={form.lat !== undefined ? String(form.lat) : ""}
        onChangeText={(text) =>
          handleChange("lat", text === "" ? 0 : Number(text))
        }
        editable={!disabled}
        keyboardType="numeric"
      />
      <CustomTextInput
        textLabel="Phone"
        value={form.phone ?? ""}
        onChangeText={(text) => handleChange("phone", text)}
        editable={!disabled}
      />
      <CustomTextInput
        textLabel="Email"
        value={form.email ?? ""}
        onChangeText={(text) => handleChange("email", text)}
        editable={!disabled}
      />
    </View>
  );
}
