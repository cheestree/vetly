import { AuthProvider, useAuth } from "@/hooks/useAuth";
import { useSegments, useRouter, Slot } from "expo-router";
import ToastManager from 'toastify-react-native'
import React, { useEffect } from "react";

function AuthGuard() {
  const { user, loading } = useAuth();
  const segments = useSegments();
  const router = useRouter();

  useEffect(() => {
    const inAuthGroup = segments[0] === "(private)";
    const isPublicPage = segments[0] === "(public)";

    if (!loading && !user && inAuthGroup) {
      router.replace("/(public)/login");
    } else if (user && isPublicPage) {
      router.replace("/(private)/(drawer)/dashboard");
    }
  }, [user, segments, loading, router]);

  return <Slot />;
}

export default function RootLayout() {
  return (
    <AuthProvider>
      <AuthGuard />
      <ToastManager useModal={true} />
    </AuthProvider>
  );
}
