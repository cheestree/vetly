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
  const [name, setName] = useState(value?.name ?? "");
  const [nif, setNif] = useState(value?.nif ?? "");
  const [address, setAddress] = useState(value?.address ?? "");

  useEffect(() => {
    onChange({
      ...value,
      name,
      nif,
      address,
      lng,
      lat,
      phone,
      email,
      services,
    });
    // eslint-disable-next-line
  }, [name, nif, address]);

  return (
    <View style={{ gap: 8 }}>
      <CustomTextInput
        textLabel="Clinic Name"
        value={name}
        onChangeText={setName}
        editable={!disabled}
      />
      <CustomTextInput
        textLabel="NIF"
        value={nif}
        onChangeText={setNif}
        editable={!disabled}
      />
      <CustomTextInput
        textLabel="Address"
        value={address}
        onChangeText={setAddress}
        editable={!disabled}
      />
    </View>
  );
}
