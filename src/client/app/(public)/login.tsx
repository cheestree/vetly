import BaseComponent from "@/components/basic/BaseComponent";
import { useAuth } from "@/hooks/useAuth";
import ROUTES from "@/lib/routes";
import { useRouter } from "expo-router";
import { useLayoutEffect } from "react";
import { Button, SafeAreaView } from "react-native";

export default function Login() {
  const router = useRouter();
  const { signIn, user } = useAuth();

  useLayoutEffect(() => {
    if (user) router.replace(ROUTES.PRIVATE.ME.DASHBOARD);
  }, []);

  return (
    <>
      <BaseComponent isLoading={false} title="Login">
        <SafeAreaView>
          <Button
            title="Sign In with Google"
            onPress={async () => {
              await signIn();
              if (user) router.push(ROUTES.PRIVATE.ME.DASHBOARD);
            }}
          />
        </SafeAreaView>
      </BaseComponent>
    </>
  );
}
