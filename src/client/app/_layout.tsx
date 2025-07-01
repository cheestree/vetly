import { AuthProvider } from "@/hooks/useAuth";
import { Slot } from "expo-router";
import React from "react";
import { GestureHandlerRootView } from "react-native-gesture-handler";
import ToastManager from "toastify-react-native";

export default function RootLayout() {
  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
      <AuthProvider>
        <ToastManager useModal={true} />
        <Slot />
      </AuthProvider>
    </GestureHandlerRootView>
  );
}
