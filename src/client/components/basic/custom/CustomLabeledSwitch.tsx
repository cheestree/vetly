import { useThemedStyles } from "@/hooks/useThemedStyles";
import { View, ViewStyle } from "react-native";
import { Switch } from "react-native-paper";
import CustomButton from "./CustomButton";
import CustomText from "./CustomText";

type CustomLabeledSwitchProps = {
  label: string;
  value: boolean | null;
  onValueChange: (newValue: boolean | null) => void;
  disabled?: boolean;
  style?: ViewStyle;
  tristate?: boolean;
};

export default function CustomLabeledSwitch({
  label,
  value,
  onValueChange,
  disabled,
  style,
  tristate = false,
}: CustomLabeledSwitchProps) {
  const { styles } = useThemedStyles();

  const cycleValue = () => {
    if (value === true) onValueChange(false);
    else if (value === false) onValueChange(null);
    else onValueChange(true);
  };

  return (
    <View style={[styles.modalField, style]}>
      <CustomText text={label} />
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
