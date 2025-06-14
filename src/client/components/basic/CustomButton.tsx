import { useThemedStyles } from "@/hooks/useThemedStyles";
import size from "@/theme/size";
import { FontAwesome5 } from "@expo/vector-icons";
import { Pressable, StyleProp, Text, View, ViewStyle } from "react-native";

type CustomButtonProps = {
  text?: string;
  icon?: React.ReactNode;
  onPress: () => void;
  disabled?: boolean | undefined;
  style?: StyleProp<ViewStyle>;
};

export default function CustomButton({
  text,
  icon,
  onPress,
  disabled = false,
  style,
}: CustomButtonProps) {
  const { styles } = useThemedStyles();

  return (
    <Pressable
      onPress={onPress}
      style={[styles.button, style]}
      disabled={disabled}
    >
      <View style={{ flexDirection: "row", alignItems: "center" }}>
        {icon && (
          <FontAwesome5
            name={icon}
            size={size.icon.sm}
            color="white"
            style={{ marginRight: text ? 8 : 0 }}
          />
        )}
        {text && <Text style={styles.buttonText}>{text}</Text>}
      </View>
    </Pressable>
  );
}
