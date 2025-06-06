import { Switch, Text } from "react-native-paper";
import { StyleProp, ViewStyle, StyleSheet } from "react-native";
import { View } from "react-native";

interface LabeledSwitchProps {
  label: string;
  value: boolean;
  disabled?: boolean;
  onValueChange: (value: boolean) => void;
  style?: StyleProp<ViewStyle>;
}

export default function LabeledSwitch(params: LabeledSwitchProps) {
  return (
    <View style={[styles.container, params.style]}>
      <Text style={styles.label}>{params.label}</Text>
      <Switch
        disabled={params.disabled}
        value={params.value}
        onValueChange={params.onValueChange}
      />
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
