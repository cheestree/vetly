import { FontAwesome5 } from "@expo/vector-icons";
import { View, ViewStyle, StyleProp } from "react-native";

type SizedIconProps = {
  icon: string;
  size?: number;
  style?: StyleProp<ViewStyle>;
};

export default function SizedIcon({ icon, size = 18, style }: SizedIconProps) {
  const defaultContainerStyle: ViewStyle = {
    width: size,
    alignItems: "center",
  };

  return (
    <View style={[defaultContainerStyle, style]}>
      <FontAwesome5 name={icon} size={size} color="black" />
    </View>
  );
}
