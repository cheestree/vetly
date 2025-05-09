import { FontAwesome } from "@expo/vector-icons";
import CustomBottomTabBar from "../tab/CustomBottomTabBar";
import Header from "../Header";
import { Stack, Tabs } from "expo-router";

const Screens = [
  {
    name: "index",
    options: {
      title: "Home",
      tabBarIcon: ({ color, size }) => (
        <FontAwesome name="home" color={color} size={size} />
      ),
    },
  },
  {
    name: "login",
    options: {
      title: "Login",
      tabBarIcon: ({ color, size }) => (
        <FontAwesome name="sign-in" color={color} size={size} />
      ),
    },
  },
  {
    name: "contact",
    options: {
      title: "Contact",
      tabBarIcon: ({ color, size }) => (
        <FontAwesome name="envelope" color={color} size={size} />
      ),
    },
  },
  {
    name: "about",
    options: {
      title: "About",
      tabBarIcon: ({ color, size }) => (
        <FontAwesome name="info" color={color} size={size} />
      ),
    },
  },
];

export function MobileNavigator() {
  return (
    <Tabs
      screenOptions={{ headerShown: false }}
      tabBar={(props) => <CustomBottomTabBar {...props} />}
    >
      {Screens.map((screen) => (
        <Tabs.Screen
          key={screen.name}
          name={screen.name}
          options={screen.options}
        />
      ))}
    </Tabs>
  );
}

export function WebNavigator() {
  return (
    <Stack
      screenOptions={{
        header: () => Header(),
        headerShown: true,
      }}
    >
      {Screens.map((screen) => (
        <Stack.Screen
          key={screen.name}
          name={screen.name}
          options={screen.options}
        />
      ))}
    </Stack>
  );
}
