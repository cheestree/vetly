import { AuthProvider, useAuth } from "@/hooks/AuthContext";
import { useSegments, useRouter, Slot } from "expo-router";
import { useEffect } from "react";

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
    </AuthProvider>
  );
}
