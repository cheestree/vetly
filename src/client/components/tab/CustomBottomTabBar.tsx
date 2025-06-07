import { RouterProps } from "@/lib/types";
import { Ionicons } from "@expo/vector-icons";
import { usePathname, useRouter } from "expo-router";
import { View, Pressable, Text, StyleSheet } from "react-native";

export default function CustomBottomTabBar({
  authenticated,
  routes,
}: RouterProps) {
  const pathname = usePathname();
  const router = useRouter();

  return (
    <View style={style.container}>
      {routes.map((route) => {
        const isFocused = pathname === route.route;

        return (
          <Pressable
            key={route.route}
            onPress={() => {
              if (!isFocused) router.push(route.route);
            }}
            style={[
              style.pressable,
              { backgroundColor: isFocused ? "#f0f0f0" : "#fff" },
            ]}
          >
            <Ionicons
              name={route.icon}
              size={20}
              color={isFocused ? "#6200ee" : "#333"}
            />
            <Text
              style={{ color: isFocused ? "#6200ee" : "#333", fontSize: 12 }}
            >
              {route.label}
            </Text>
          </Pressable>
        );
      })}
    </View>
  );
}

const style = StyleSheet.create({
  container: {
    flexDirection: "row",
    borderTopWidth: 1,
    borderTopColor: "#ccc",
  },
  pressable: {
    flex: 1,
    paddingVertical: 8,
    alignItems: "center",
  },
});
