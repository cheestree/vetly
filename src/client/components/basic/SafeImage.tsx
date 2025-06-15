import { useState } from "react";
import { Image, ImageProps, ImageSourcePropType } from "react-native";

type SafeImageProps = {
  uri?: string;
  fallback?: ImageSourcePropType;
} & ImageProps;

export default function SafeImage({ uri, fallback, ...props }: SafeImageProps) {
  const [error, setError] = useState(false);

  return (
    <Image
      source={uri && !error ? { uri } : fallback}
      onError={() => setError(true)}
      {...props}
    />
  );
}
