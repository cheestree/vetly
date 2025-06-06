import { useDocumentTitle } from "@/hooks/useDocumentTitle";
import React, { ReactNode, useEffect, useLayoutEffect } from "react";
import { Animated, ActivityIndicator, ViewStyle, Platform } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import Head from "expo-router/head";
import { usePathname } from "expo-router";
import Drawer from "expo-router/drawer";

interface BaseComponentProps {
  isLoading: boolean;
  children: ReactNode;
  style?: ViewStyle;
  title?: string;
}

export default function BaseComponent({
  isLoading,
  children,
  style,
  title = "Vetly",
}: BaseComponentProps) {
  const fallbackStyle = {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  } as ViewStyle;

  return isLoading ? (
    <Animated.View style={style ?? fallbackStyle}>
      <ActivityIndicator
        animating={true}
        color="#0000ff"
        size={64}
        style={{ flex: 1, alignContent: "center" }}
      />
    </Animated.View>
  ) : (
    <SafeAreaView style={style ?? { flex: 1 }}>
      <Head>
        <title>{title}</title>
      </Head>
      <Drawer.Screen options={{ headerTitle: title }} />
      {children}
    </SafeAreaView>
  );
}
