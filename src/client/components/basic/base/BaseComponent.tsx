import { useAuth } from "@/hooks/useAuth";
import { useDocumentTitle } from "@/hooks/useDocumentTitle";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { checkRouteAccess } from "@/lib/utils";
import { Redirect, useSegments } from "expo-router";
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
  const { loading: authLoading, information } = useAuth();
  const segments = useSegments();
  const [internalLoading, setInternalLoading] = useState(false);

  useDocumentTitle(title);

  const shouldShowLoading =
    isLoading !== undefined ? isLoading : authLoading || internalLoading;

  useEffect(() => {
    if (authLoading || !fetchOperation) return;

    setInternalLoading(true);
    fetchOperation()
      .catch((e) => {
        console.error("Fetch operation failed:", e);
      })
      .finally(() => {
        setInternalLoading(false);
      });
  }, [authLoading]);

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

  if (!authLoading && !information) {
    return <Redirect href="/login" />;
  }

  const canProceed = checkRouteAccess(segments, information.roles);
  if (!canProceed) {
    return <Redirect href="/dashboard" />;
  }

  return (
    <SafeAreaView style={[...(baseStyle ? [baseStyle] : []), styles.container]}>
      <Drawer.Screen options={{ headerTitle: title }} />
      {typeof children === "function" ? children() : children}
    </SafeAreaView>
  );
}
