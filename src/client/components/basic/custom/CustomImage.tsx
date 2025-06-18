import { useThemedStyles } from "@/hooks/useThemedStyles";
import { Image, Pressable, StyleProp, ViewStyle } from "react-native";

type CustomImageProps = {
  label?: string;
  url?: string | null;
  onPress?: () => void;
  style?: StyleProp<ViewStyle>;
};

export default function CustomImage({
  label,
  url,
  onPress,
  style,
}: CustomImageProps) {
  const { styles } = useThemedStyles();

  return (
    <Pressable onPress={onPress} style={styles.imageContainer}>
      <Image
        source={url ? { uri: url } : require("@/assets/placeholder.png")}
        style={styles.imageContainer}
        resizeMode="cover"
        aria-label={label}
      />
    </Pressable>
  );
}
