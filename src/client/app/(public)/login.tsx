import BasePage from "@/components/basic/base/BasePage";
import CustomButton from "@/components/basic/custom/CustomButton";
import UserSignInContent from "@/components/user/UserSignInContent";
import UserSignUpContent from "@/components/user/UserSignUpContent";
import { useAuth } from "@/hooks/useAuth";
import ROUTES from "@/lib/routes";
import size from "@/theme/size";
import { useRouter } from "expo-router";
import { useLayoutEffect, useState } from "react";
import { StyleSheet, View } from "react-native";

export default function Login() {
  const router = useRouter();
  const { user } = useAuth();
  const [isSignUp, setIsSignUp] = useState(false);

  useLayoutEffect(() => {
    if (user) router.replace(ROUTES.PRIVATE.ME.DASHBOARD);
  }, [user]);

  return (
    <BasePage>
      <View style={extras.outer}>
        <View style={extras.container}>
          {isSignUp ? (
            <UserSignUpContent
              onSuccess={() => router.replace(ROUTES.PRIVATE.ME.DASHBOARD)}
            />
          ) : (
            <UserSignInContent
              onSuccess={() => router.replace(ROUTES.PRIVATE.ME.DASHBOARD)}
            />
          )}
          <CustomButton
            onPress={() => setIsSignUp((prev) => !prev)}
            text={
              isSignUp
                ? "Already have an account? Sign In"
                : "Don't have an account? Sign Up"
            }
          />
        </View>
      </View>
    </BasePage>
  );
}

const extras = StyleSheet.create({
  outer: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
  container: {
    padding: size.padding.xl,
    gap: size.gap.md,
    width: 400,
  },
});
