import { useThemedStyles } from "@/hooks/useThemedStyles";
import { ReactNode } from "react";
import { View } from "react-native";

export default function OverlayContainer({
  children,
}: {
  children: ReactNode;
}) {
  const { styles } = useThemedStyles();

  return <View style={styles.floatingContainer}>{children}</View>;
}
