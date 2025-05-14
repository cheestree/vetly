import React from "react";
import { useWindowDimensions, Platform } from "react-native";
import { MobileNavigator, WebNavigator } from "./PlatformNavigator";

export default function PublicNavigator() {
  const { width } = useWindowDimensions();
  const isDesktop = Platform.OS === "web" && width >= 768;

  return isDesktop ? <WebNavigator /> : <MobileNavigator />;
}
