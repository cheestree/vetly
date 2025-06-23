import { useEffect, useState } from "react";
import { View } from "react-native";
import CustomTextInput from "../../basic/custom/CustomTextInput";

type Props = {
  value: { clinicId?: number };
  onChange: (v: { clinicId?: number }) => void;
  disabled?: boolean;
};

export default function ClinicMembershipCreateForm({
  value,
  onChange,
  disabled,
}: Props) {
  const [clinicId, setClinicId] = useState(
    value?.clinicId ? String(value.clinicId) : "",
  );

  useEffect(() => {
    onChange({ clinicId: clinicId ? Number(clinicId) : undefined });
  }, [clinicId]);

  return (
    <View style={{ gap: 8 }}>
      <CustomTextInput
        textLabel="Clinic ID"
        value={clinicId}
        onChangeText={setClinicId}
        keyboardType="numeric"
        editable={!disabled}
      />
    </View>
  );
}
