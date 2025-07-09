import { useThemedStyles } from "@/hooks/useThemedStyles";
import { Pressable, StyleProp, ViewStyle } from "react-native";
import SafeImage from "../SafeImage";

type CustomImageProps = {
  label?: string;
  url?: string;
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
      <SafeImage uri={url} resizeMode="cover" aria-label={label} />
    </Pressable>
  );
}
