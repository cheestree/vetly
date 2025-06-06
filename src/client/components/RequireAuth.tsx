import { useAuth } from "@/hooks/useAuth";
import { useRouter } from "expo-router";
import { useEffect } from "react";
import { ActivityIndicator } from "react-native-paper";

export default function RequireAuth({
  children,
}: {
  children: React.ReactNode;
}) {
  const { user, loading } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!loading && !user) {
      router.replace("/(public)/login");
    }
  }, [loading, user]);

  if (loading || !user)
    return <ActivityIndicator animating={true} size="large" />;

  return <>{children}</>;
}
