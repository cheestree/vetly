import layout from "@/theme/layout";
import size from "@/theme/size";
import { FontAwesome5 } from "@expo/vector-icons";
import { Pressable, StyleProp, Text, View, ViewStyle } from "react-native";

type CustomButtonProps = {
  text?: string;
  icon?: React.ReactNode;
  onPress: () => void;
  style?: StyleProp<ViewStyle>;
};

export default function CustomButton({
  text,
  icon,
  onPress,
  style,
}: CustomButtonProps) {
  return (
    <Pressable onPress={onPress} style={[layout.button, style]}>
      <View style={{ flexDirection: "row", alignItems: "center" }}>
        {icon && (
          <FontAwesome5
            name={icon}
            size={size.icon.sm}
            color="white"
            style={{ marginRight: text ? 8 : 0 }}
          />
        )}
        {text && <Text style={layout.buttonText}>{text}</Text>}
      </View>
    </Pressable>
  );
}
