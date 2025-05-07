import Home from "@/app/index";
import About from "@/app/about";
import Contact from "@/app/contact";
import Login from "@/app/login";
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import React from "react";
import CustomBottomTabBar from "../tab/CustomBottomTabBar";
import { FontAwesome } from "@expo/vector-icons";
import { createStackNavigator } from '@react-navigation/stack'
import { useWindowDimensions, Platform } from "react-native";
import Header from "../Header";

const PublicTabs = createBottomTabNavigator();
const Stack = createStackNavigator();
const Screens = [
	{
		name: 'index',
		component: Home,
		options: {
			title: 'Home',
			tabBarIcon: ({ color, size }) => <FontAwesome name="home" color={color} size={size} />
		}
	},
	{
		name: 'login',
		component: Login,
		options: {
			title: 'Login',
			tabBarIcon: ({ color, size }) => <FontAwesome name="sign-in" color={color} size={size} />
		}
	},
	{
		name: 'contact',
		component: Contact,
		options: {
			title: 'Contact',
			tabBarIcon: ({ color, size }) => <FontAwesome name="envelope" color={color} size={size} />
		}
	},
	{
		name: 'about',
		component: About,
		options: {
			title: 'About',
			tabBarIcon: ({ color, size }) => <FontAwesome name="info" color={color} size={size} />
		}
	}
]

export default function PublicNavigator() {
	const { width } = useWindowDimensions()
	const isDesktop = Platform.OS === 'web' && width >= 768

  return isDesktop ? <WebNavigator /> : <MobileNavigator />
}

function MobileNavigator() {
	return (
		<PublicTabs.Navigator 
      screenOptions={{ headerShown: false }}  
      tabBar={(props) => <CustomBottomTabBar {...props} />}
    >
			{Screens.map((screen) => (
				<PublicTabs.Screen
					key={screen.name}
					name={screen.name}
					component={screen.component}
					options={screen.options}
				/>
			))}
		</PublicTabs.Navigator>
	)
}

function WebNavigator() {
  return (
    <Stack.Navigator
      screenOptions={{
        header: () => Header(),
        headerShown: true,
      }}
    >
      {Screens.map((screen) => (
        <Stack.Screen
          key={screen.name}
          name={screen.name}
          component={screen.component}
          options={screen.options}
        />
      ))}
    </Stack.Navigator>
  )
}

/*
export default function PublicNavigator() {
	return (
		<Tabs>
			<Tabs.Screen 
				name="index" 
				options={{ title: 'Home' }}
			/>
			<Tabs.Screen 
				name="login" 
				options={{ title: 'Login' }}
			/>
			<Tabs.Screen 
				name="contact" 
				options={{ title: 'Contact' }}
			/>
			<Tabs.Screen 
				name="about" 
				options={{ title: 'About' }}
			/>
		</Tabs>
	)
}
*/