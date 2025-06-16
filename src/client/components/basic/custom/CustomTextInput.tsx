import { useThemedStyles } from "@/hooks/useThemedStyles";
import { ViewStyle } from "react-native";
import { TextInput } from "react-native-gesture-handler";
import { TextInputProps } from "react-native-paper";

type CustomTextInputProps = TextInputProps & {
  style?: ViewStyle;
};

export default function CustomTextInput({
  placeholder,
  value,
  onChangeText,
  keyboardType,
  style,
}: CustomTextInputProps) {
  const { styles } = useThemedStyles();

  return (
    <TextInput
      placeholder={placeholder}
      value={value}
      onChangeText={onChangeText}
      keyboardType={keyboardType}
      style={[styles.textInput, style]}
    />
  );
}
