import { useEffect, useState } from "react";
import { View } from "react-native";
import CustomTextInput from "../../basic/custom/CustomTextInput";

type Props = {
  value: { roleName?: string };
  onChange: (v: { roleName?: string }) => void;
  disabled?: boolean;
};

export default function RoleUpdateForm({ value, onChange, disabled }: Props) {
  const [role, setRole] = useState(value?.roleName ?? "");

  useEffect(() => {
    onChange({ roleName: role });
  }, [role]);

  return (
    <View style={{ gap: 8 }}>
      <CustomTextInput
        textLabel="Role Name"
        value={role}
        onChangeText={setRole}
        editable={!disabled}
      />
    </View>
  );
}
