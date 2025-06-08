import React, { ReactNode } from "react";
import { Animated, ActivityIndicator, ViewStyle, StyleSheet } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import Head from "expo-router/head";
import Drawer from "expo-router/drawer";
import layout from "@/theme/layout";
import spacing from "@/theme/spacing";
import { useDocumentTitle } from "@/hooks/useDocumentTitle";

interface BaseComponentProps {
  isLoading: boolean;
  children: ReactNode;
  baseStyle?: ViewStyle;
  title?: string;
}

export default function BaseComponent({
  isLoading,
  children,
  baseStyle,
  title = "Vetly",
}: BaseComponentProps) {
  useDocumentTitle(title)

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
      {children}
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
})