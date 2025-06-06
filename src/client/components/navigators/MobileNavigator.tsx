import CustomBottomTabBar from "@/components/tab/CustomBottomTabBar";
import { Tabs } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { RouterProps } from "@/lib/types";

export function MobileNavigator({ authenticated, routes }: RouterProps) {
  const filteredTabs = routes.filter((tab) => {
    if (tab.route === "/login" && authenticated) return false;
    if (tab.route === "/me/dashboard" && !authenticated) return false;
    return true;
  });

  return (
    <Tabs
      screenOptions={{
        headerShown: false,
      }}
      tabBar={(props) => (
        <CustomBottomTabBar routes={filteredTabs} {...props} />
      )}
    >
      {filteredTabs.map(({ name, tabBarLabel, icon }) => (
        <Tabs.Screen
          key={name}
          name={name}
          options={{
            tabBarLabel,
            tabBarIcon: ({ color, size }) => (
              <Ionicons name={icon} size={size} color={color} />
            ),
          }}
        />
      ))}
    </Tabs>
  );
}
