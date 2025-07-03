import BasePage from "@/components/basic/base/BasePage";
import CustomButton from "@/components/basic/custom/CustomButton";
import UserSignInContent from "@/components/user/UserSignInContent";
import UserSignUpContent from "@/components/user/UserSignUpContent";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import ROUTES from "@/lib/routes";
import { useRouter } from "expo-router";
import { useState } from "react";
import { StyleSheet, View } from "react-native";

export default function Login() {
  const { styles } = useThemedStyles();
  const router = useRouter();
  const [isSignUp, setIsSignUp] = useState(false);

  return (
    <BasePage>
      <View style={[styles.container, extras.container]}>
        <View style={styles.innerContainer}>
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
  container: {
    alignSelf: "center",
    justifyContent: "center",
  },
});
