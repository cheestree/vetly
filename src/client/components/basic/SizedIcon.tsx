import { FontAwesome5 } from "@expo/vector-icons";
import { StyleProp, View, ViewStyle } from "react-native";

type SizedIconProps = {
  icon: string;
  size?: number;
  style?: StyleProp<ViewStyle>;
  colour?: string;
};

export default function SizedIcon({
  icon,
  size = 18,
  style,
  colour = "black",
}: SizedIconProps) {
  const defaultContainerStyle: ViewStyle = {
    width: size,
    alignItems: "center",
  };

  return (
    <View style={[defaultContainerStyle, style]}>
      <FontAwesome5 name={icon} size={size} color={colour} />
    </View>
  );
}
