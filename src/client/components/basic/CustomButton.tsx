import { useThemedStyles } from "@/hooks/useThemedStyles";
import size from "@/theme/size";
import { FontAwesome5 } from "@expo/vector-icons";
import {
  Pressable,
  StyleProp,
  StyleSheet,
  Text,
  ViewStyle,
} from "react-native";

type CustomButtonProps = {
  text?: string;
  icon?: React.ReactNode;
  onPress: () => void;
  disabled?: boolean | undefined;
  fullWidth?: boolean;
  style?: StyleProp<ViewStyle>;
};

export default function CustomButton({
  text,
  icon,
  onPress,
  disabled = false,
  fullWidth = false,
  style,
}: CustomButtonProps) {
  const { colours, styles } = useThemedStyles();

  return (
    <Pressable
      onPress={onPress}
      style={[styles.button, fullWidth && { width: "100%" }, style]}
      disabled={disabled}
    >
      {icon && (
        <FontAwesome5 name={icon} size={size.icon.sm} color={styles.icon} />
      )}
      {text && <Text style={[styles.meta, icon && extras.button]}>{text}</Text>}
    </Pressable>
  );
}

const extras = StyleSheet.create({
  button: {
    marginLeft: size.margin.sm,
  },
});
