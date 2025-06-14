import { useAuth } from "@/hooks/useAuth";
import { useDocumentTitle } from "@/hooks/useDocumentTitle";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import Drawer from "expo-router/drawer";
import React, { ReactNode } from "react";
import { ActivityIndicator, ViewStyle } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

type BaseComponentProps = {
  isLoading?: boolean;
  children: (() => ReactNode) | ReactNode;
  baseStyle?: ViewStyle;
  title?: string;
};

export default function BaseComponent({
  isLoading,
  children,
  baseStyle,
  title = "Vetly",
}: BaseComponentProps) {
  const { styles } = useThemedStyles();
  const { loading: authLoading } = useAuth();

  useDocumentTitle(title);

  const shouldShowLoading = isLoading !== undefined ? isLoading : authLoading;

  if (shouldShowLoading) {
    return (
      <ActivityIndicator
        animating={true}
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
