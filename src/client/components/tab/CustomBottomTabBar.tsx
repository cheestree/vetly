import { useThemedStyles } from "@/hooks/useThemedStyles";
import { RouterProps } from "@/lib/types";
import size from "@/theme/size";
import { FontAwesome5 } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { useState } from "react";
import { Pressable, View } from "react-native";

export default function CustomBottomTabBar({
  authenticated,
  routes,
}: RouterProps) {
  const { styles, colours } = useThemedStyles();
  const [key, setKey] = useState("");
  const router = useRouter();

  return (
    <View style={styles.bottomBar}>
      {routes.map((route) => {
        const isFocused = key === route.route;

        return (
          <Pressable
            key={route.route}
            onPress={() => {
              if (!isFocused) {
                router.push(route.route);
                setKey(route.route);
              }
            }}
            style={[
              styles.bottomBarButton,
              isFocused
                ? { backgroundColor: colours.primaryBackground }
                : { backgroundColor: colours.secondaryBackground },
            ]}
          >
            <FontAwesome5
              name={route.icon}
              style={styles.icon}
              size={size.icon.sm}
            />
          </Pressable>
        );
      })}
    </View>
  );
}
