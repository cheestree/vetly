import { Pressable, StyleSheet, View, ViewStyle } from "react-native";
import { Switch, Text } from "react-native-paper";

interface LabeledSwitchProps {
  label: string;
  value: boolean | null;
  onValueChange: (newValue: boolean | null) => void;
  disabled?: boolean;
  style?: ViewStyle;
  tristate?: boolean;
}

export default function LabeledSwitch({
  label,
  value,
  onValueChange,
  disabled,
  style,
  tristate = false,
}: LabeledSwitchProps) {
  const cycleValue = () => {
    if (value === true) onValueChange(false);
    else if (value === false) onValueChange(null);
    else onValueChange(true);
  };

  return (
    <View style={[styles.container, style]}>
      <Text style={styles.label}>{label}</Text>
      {tristate ? (
        <Pressable onPress={cycleValue} disabled={disabled}>
          {value === true ? "Yes" : value === false ? "No" : "Any"}
        </Pressable>
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

const styles = StyleSheet.create({
  container: {
    flexDirection: "row",
    alignItems: "center",
    verticalAlign: "middle",
    justifyContent: "space-between",
  },
  label: {
    fontSize: 16,
    marginRight: 8,
  },
});
