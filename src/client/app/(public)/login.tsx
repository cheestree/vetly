import BaseComponent from "@/components/basic/BaseComponent";
import { useAuth } from "@/hooks/useAuth";
import ROUTES from "@/lib/routes";
import colours from "@/theme/colours";
import size from "@/theme/size";
import { useRouter } from "expo-router";
import { useLayoutEffect } from "react";
import { Pressable, SafeAreaView, StyleSheet, Text } from "react-native";

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
          <Pressable
            onPress={async () => {
              await signIn();
              if (user) router.push(ROUTES.PRIVATE.ME.DASHBOARD);
            }}
            style={styles.headerButton}
          >
            <Text style={styles.headerButtonText}>Login with Google</Text>
          </Pressable>
        </SafeAreaView>
      </BaseComponent>
    </>
  );
}

const styles = StyleSheet.create({
  headerButton: {
    backgroundColor: colours.primary,
    borderRadius: size.border.sm,
    flexDirection: "row",
    alignItems: "center",
    padding: size.padding.sm,
  },
  headerButtonText: {
    alignItems: "center",
    color: colours.fontThirdiary,
  },
});
