import { useThemedStyles } from "@/hooks/useThemedStyles";
import { View, ViewStyle } from "react-native";
import { Switch, Text } from "react-native-paper";
import CustomButton from "./CustomButton";

type LabeledSwitchProps = {
  label: string;
  value: boolean | null;
  onValueChange: (newValue: boolean | null) => void;
  disabled?: boolean;
  style?: ViewStyle;
  tristate?: boolean;
};

export default function LabeledSwitch({
  label,
  value,
  onValueChange,
  disabled,
  style,
  tristate = false,
}: LabeledSwitchProps) {
  const { styles } = useThemedStyles();

  const cycleValue = () => {
    if (value === true) onValueChange(false);
    else if (value === false) onValueChange(null);
    else onValueChange(true);
  };

  return (
    <View style={[styles.container, style]}>
      <Text style={styles.label}>{label}</Text>
      {tristate ? (
        <CustomButton
          onPress={cycleValue}
          disabled={disabled}
          text={value === true ? "Yes" : value === false ? "No" : "Any"}
        />
      ) : (
        <Switch
          disabled={disabled}
          value={!!value}
          onValueChange={(newVal) => onValueChange(newVal)}
        />
      )}
    </View>
  );
}
