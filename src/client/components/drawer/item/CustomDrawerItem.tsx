import { ReactNode, useState } from "react";
import { Pressable, StyleProp, Text, TextStyle, ViewStyle } from "react-native";

type CustomDrawerItemProps = {
  label: string;
  onPress: () => void;
  icon: ReactNode;
  style?: StyleProp<ViewStyle>;
  labelStyle?: StyleProp<TextStyle>;
};

export default function CustomDrawerItem({
  label,
  onPress,
  icon,
  style,
  labelStyle,
}: CustomDrawerItemProps) {
  const [hovered, setHovered] = useState(false);

  return (
    <Pressable
      onHoverIn={() => setHovered(true)}
      onHoverOut={() => setHovered(false)}
      onPress={onPress}
      style={[
        {
          backgroundColor: hovered ? "#f0f0f0" : "transparent",
          borderRadius: 12,
          padding: 12,
          flexDirection: "row",
          alignItems: "center",
          gap: 10,
        },
        style,
      ]}
    >
      {icon}
      <Text style={labelStyle}>{label}</Text>
    </Pressable>
  );
}
