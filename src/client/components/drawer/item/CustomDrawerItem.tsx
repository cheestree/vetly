import { useThemedStyles } from "@/hooks/useThemedStyles";
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
  const { colours, styles } = useThemedStyles();
  const [hovered, setHovered] = useState(false);

  return (
    <Pressable
      onHoverIn={() => setHovered(true)}
      onHoverOut={() => setHovered(false)}
      onPress={onPress}
      style={[
        style,
        {
          backgroundColor: hovered ? "#f0f0f0" : "transparent",
        },
        style,
      ]}
    >
      {icon}
      <Text style={[styles.meta, labelStyle]}>{label}</Text>
    </Pressable>
  );
}
