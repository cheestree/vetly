import { AuthProvider, useAuth } from "@/hooks/AuthContext";
import { useSegments, useRouter, Slot } from "expo-router";
import { useEffect } from "react";

function AuthGuard() {
  const { user, loading } = useAuth();
  const segments = useSegments();
  const router = useRouter();

  useEffect(() => {
    console.log('AuthGuard updated:', { user, loading, segments });
  
    const inAuthGroup = segments[0] === '(private)';
    const isPublicPage = segments[0] === '(public)';
  
    if (!loading && !user && inAuthGroup) {
      console.log('Redirecting to login');
      router.replace('/(public)/login');
    } else if (user && isPublicPage) {
      console.log('Redirecting to dashboard');
      router.replace('/(private)/dashboard');
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
  
