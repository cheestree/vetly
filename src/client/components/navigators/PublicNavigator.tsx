import React from "react";
import { useWindowDimensions, Platform } from "react-native";
import { MobileNavigator, WebNavigator } from "./PlatformNavigator";

export default function PublicNavigator() {
  const { width } = useWindowDimensions();
  const isDesktop = Platform.OS === "web" && width >= 768;

  return isDesktop ? <WebNavigator /> : <MobileNavigator />;
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
