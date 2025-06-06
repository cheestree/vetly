import { AuthProvider } from "@/hooks/useAuth";
import ToastManager from "toastify-react-native";
import React from "react";
import { Slot } from "expo-router";

export default function RootLayout() {
  return (
    <AuthProvider>
      <ToastManager useModal={true} />
      <Slot />
    </AuthProvider>
  );
}
