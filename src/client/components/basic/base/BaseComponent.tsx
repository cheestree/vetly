import { useDocumentTitle } from "@/hooks/useDocumentTitle";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { Drawer } from "expo-router/drawer";
import { ReactNode, useEffect, useState } from "react";
import {
  ActivityIndicator,
  SafeAreaView,
  StyleProp,
  ViewStyle,
} from "react-native";

type BaseComponentProps = {
  isLoading?: boolean;
  title?: string;
  baseStyle?: StyleProp<ViewStyle>;
  children: ReactNode | (() => ReactNode);
  fetchOperation?: () => Promise<void>;
};

export default function BaseComponent({
  isLoading,
  children,
  baseStyle,
  title = "Vetly",
  fetchOperation,
}: BaseComponentProps) {
  const { styles } = useThemedStyles();
  const [internalLoading, setInternalLoading] = useState(false);

  useDocumentTitle(title);

  const shouldShowLoading =
    isLoading !== undefined ? isLoading : internalLoading;

  useEffect(() => {
    if (!fetchOperation) return;

    setInternalLoading(true);
    fetchOperation()
      .catch((e) => {
        console.error("Fetch operation failed:", e);
      })
      .finally(() => {
        setInternalLoading(false);
      });
  }, [fetchOperation]);

  if (shouldShowLoading) {
    return (
      <ActivityIndicator
        animating
        color="#0000ff"
        size={64}
        style={styles.loader}
      />
    );
  }

  return (
    <SafeAreaView style={[...(baseStyle ? [baseStyle] : []), styles.container]}>
      <Drawer.Screen options={{ headerTitle: title }} />
      {typeof children === "function" ? children() : children}
    </SafeAreaView>
  );
}
