import React, { ReactNode } from "react";
import { Animated, ActivityIndicator, ViewStyle } from "react-native";

interface BaseComponentProps {
  isLoading: boolean;
  children: ReactNode;
  style?: ViewStyle;
}

export default function BaseComponent({
  isLoading,
  children,
  style,
}: BaseComponentProps) {
  const fallbackStyle = {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  } as ViewStyle;

  return isLoading ? (
    <Animated.View style={style ?? fallbackStyle}>
      <ActivityIndicator size="large" color="#0000ff" />
    </Animated.View>
  ) : (
    <>{children}</>
  );
}
