import { useThemedStyles } from "@/hooks/useThemedStyles";
import { ReactNode, useState } from "react";
import { Pressable, StyleProp, Text, TextStyle, ViewStyle } from "react-native";

type CustomDrawerItemProps = {
  label: string;
  onPress: () => void;
  icon: ReactNode;
  style?: StyleProp<ViewStyle>;
  labelStyle?: StyleProp<TextStyle>;
  showLabel?: boolean;
};

export default function CustomDrawerItem({
  label,
  onPress,
  icon,
  style,
  labelStyle,
  showLabel = true,
}: CustomDrawerItemProps) {
  const { styles } = useThemedStyles();
  const [hovered, setHovered] = useState(false);

  return (
    <Pressable
      onHoverIn={() => setHovered(true)}
      onHoverOut={() => setHovered(false)}
      onPress={onPress}
      style={[
        {
          flexDirection: "row",
          alignItems: "center",
          gap: showLabel ? 12 : 0,
          padding: 12,
          borderRadius: 8,
          backgroundColor: hovered ? "#f0f0f0" : "transparent",
        },
        style,
      ]}
    >
      {icon}
      {showLabel && <Text style={[styles.meta, labelStyle]}>{label}</Text>}
    </Pressable>
  );
}
