import CustomBottomTabBar from "@/components/tab/CustomBottomTabBar";
import { RouterProps } from "@/lib/types";
import { FontAwesome5 } from "@expo/vector-icons";
import { Tabs } from "expo-router";

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
      {filteredTabs.map(({ name, label, icon }) => (
        <Tabs.Screen
          key={name}
          name={name}
          options={{
            tabBarLabel: label,
            tabBarIcon: ({ color, size }) => (
              <FontAwesome5 name={icon} size={size} color={color} />
            ),
          }}
        />
      ))}
    </Tabs>
  );
}
