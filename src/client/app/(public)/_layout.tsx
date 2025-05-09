import { AuthProvider, useAuth } from "@/hooks/AuthContext";
import React, { useEffect } from "react";
import { ActivityIndicator, View } from "react-native";
import PrivateNavigator from "@/components/navigators/PrivateNavigator";
import PublicNavigator from "@/components/navigators/PublicNavigator";
import Animated, {
  useAnimatedStyle,
  useSharedValue,
  withTiming,
} from "react-native-reanimated";

export default function Layout() {
  return (
    <LayoutContent />
  );
}

function LayoutContent() {
  const { user, loading } = useAuth();
  const opacity = useSharedValue(0);

  useEffect(() => {
    if (!loading) {
      opacity.value = withTiming(1, { duration: 500 });
    }
  }, [loading]);

  const animatedStyle = useAnimatedStyle(() => ({
    opacity: opacity.value,
  }));

  if (loading) {
    return (
      <Animated.View
        style={{ flex: 1, justifyContent: "center", alignItems: "center" }}
      >
        <ActivityIndicator size="large" color="#0000ff" />
      </Animated.View>
    );
  }

  return (
    <Animated.View style={[{ flex: 1 }, animatedStyle]}>
      {user ? <PrivateNavigator /> : <PublicNavigator />}
    </Animated.View>
  );
}

/*
function LayoutContent() {
	const { user } = useAuth()

	return (
		<>
			<Stack>
				<Stack.Protected guard={user ? false : true}>
					<Stack.Screen name="login" />
				</Stack.Protected>
				<Stack.Protected guard={user ? true : false}>
					<Stack.Screen name="private" />
				</Stack.Protected>
			</Stack>
		</>
	)
}
*/
