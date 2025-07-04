import { useThemedStyles } from "@/hooks/useThemedStyles";
import { useState } from "react";
import { Image, ImageProps } from "react-native";

type SafeImageProps = {
  uri?: string;
} & ImageProps;

export default function SafeImage({ uri, ...props }: SafeImageProps) {
  const { styles } = useThemedStyles();
  const [error, setError] = useState(false);

  return (
    <Image
      source={uri && !error ? { uri } : require("@/assets/placeholder.png")}
      onError={() => setError(true)}
      style={styles.image}
      resizeMode="cover"
      {...props}
    />
  );
}
