import { useThemedStyles } from "@/hooks/useThemedStyles";
import size from "@/theme/size";
import { FontAwesome5 } from "@expo/vector-icons";
import {
  Pressable,
  StyleSheet,
  Text,
  TextStyle,
  ViewStyle,
} from "react-native";

type CustomButtonProps = {
  text?: string;
  icon?: React.ReactNode;
  onPress: () => void;
  disabled?: boolean | undefined;
  fullWidth?: boolean;
  stylePressable?: ViewStyle;
  styleText?: TextStyle;
};

export default function CustomButton({
  text,
  icon,
  onPress,
  disabled = false,
  fullWidth = false,
  stylePressable,
  styleText,
}: CustomButtonProps) {
  const { styles } = useThemedStyles();

  return (
    <Pressable
      onPress={onPress}
      style={[
        styles.button,
        fullWidth && { width: "100%" },
        disabled && { opacity: 0.5 },
        stylePressable,
      ]}
      disabled={disabled}
    >
      {icon && (
        <FontAwesome5 name={icon} size={size.icon.sm} color={styles.icon} />
      )}
      {text && (
        <Text style={[styles.meta, icon && extras.button, styleText]}>
          {text}
        </Text>
      )}
    </Pressable>
  );
}

const extras = StyleSheet.create({
  button: {
    marginLeft: size.margin.sm,
  },
});
