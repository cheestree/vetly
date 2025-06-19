import { useThemedStyles } from "@/hooks/useThemedStyles";
import { ViewStyle } from "react-native";
import { TextInput } from "react-native-gesture-handler";
import { TextInputProps } from "react-native-paper";
import CustomText from "./CustomText";

type CustomTextInputProps = TextInputProps & {
  textLabel?: string;
  style?: ViewStyle;
};

export default function CustomTextInput({
  textLabel,
  placeholder,
  value,
  onChangeText,
  keyboardType,
  style,
  maxLength,
}: CustomTextInputProps) {
  const { styles } = useThemedStyles();

  return (
    <>
      {textLabel && <CustomText text={textLabel} style={styles.meta} />}
      <TextInput
        placeholder={placeholder}
        value={value}
        onChangeText={onChangeText}
        keyboardType={keyboardType}
        style={[styles.textInput, style]}
        maxLength={maxLength}
      />
    </>
  );
}
