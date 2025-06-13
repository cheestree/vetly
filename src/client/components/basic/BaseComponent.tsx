import { useDocumentTitle } from "@/hooks/useDocumentTitle";
import layout from "@/theme/layout";
import spacing from "@/theme/spacing";
import Drawer from "expo-router/drawer";
import React, { ReactNode } from "react";
import {
  ActivityIndicator,
  Animated,
  StyleSheet,
  ViewStyle,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

interface BaseComponentProps {
  isLoading: boolean;
  children: (() => ReactNode) | ReactNode;
  baseStyle?: ViewStyle;
  title?: string;
}

export default function BaseComponent({
  isLoading,
  children,
  baseStyle,
  title = "Vetly",
}: BaseComponentProps) {
  useDocumentTitle(title);

  return isLoading ? (
    <Animated.View style={style.loader}>
      <ActivityIndicator
        animating={true}
        color="#0000ff"
        size={64}
        style={{ flex: 1, alignContent: "center" }}
      />
    </Animated.View>
  ) : (
    <SafeAreaView style={[...(baseStyle ? [baseStyle] : []), style.container]}>
      <Drawer.Screen options={{ headerTitle: title }} />
      {typeof children === "function" ? children() : children}
    </SafeAreaView>
  );
}

const style = StyleSheet.create({
  loader: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
  container: {
    ...layout.container,
    flex: 1,
    padding: spacing.md,
  },
});
