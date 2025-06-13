import layout from "@/theme/layout";
import { Image, Pressable, StyleProp, ViewStyle } from "react-native";

type CustomImageProps = {
  label?: string;
  url?: string;
  onPress: () => void;
  style?: StyleProp<ViewStyle>;
};

export default function CustomImage({
  label,
  url,
  onPress,
  style,
}: CustomImageProps) {
  return (
    <Pressable onPress={onPress} style={layout.imageContainer}>
      <Image
        source={url ? { uri: url } : require("@/assets/placeholder.png")}
        style={layout.imageContainer}
        resizeMode="cover"
        aria-label={label}
      />
    </Pressable>
  );
}
