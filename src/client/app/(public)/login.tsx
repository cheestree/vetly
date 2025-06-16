import BaseComponent from "@/components/basic/base/BaseComponent";
import CustomButton from "@/components/basic/custom/CustomButton";
import { useAuth } from "@/hooks/useAuth";
import ROUTES from "@/lib/routes";
import { useRouter } from "expo-router";
import { useLayoutEffect } from "react";
import { SafeAreaView } from "react-native";

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
          <CustomButton
            onPress={async () => {
              await signIn();
              if (user) router.push(ROUTES.PRIVATE.ME.DASHBOARD);
            }}
            text="Login with Google"
          />
        </SafeAreaView>
      </BaseComponent>
    </>
  );
}
