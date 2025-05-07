import { Drawer } from 'expo-router/drawer';
import { GestureHandlerRootView } from 'react-native-gesture-handler';
import React from "react";
import { createDrawerNavigator } from '@react-navigation/drawer';
import Settings from '@/app/(private)/settings';
import Profile from '@/app/(private)/profile';
import Checkups from '@/app/(private)/checkups';
import Dashboard from '@/app/(private)/dashboard';
import CustomDrawerContent from '../drawer/CustomDrawerContent';
import { useWindowDimensions } from 'react-native';
import About from '@/app/about';
import Contact from '@/app/contact';


const PrivateDrawer = createDrawerNavigator()
const Screens = [
    { name: '(private)/dashboard', component: Dashboard, options: { title: 'Dashboard' } },
    { name: '(private)/settings', component: Settings, options: { title: 'Settings' } },
    { name: '(private)/profile', component: Profile, options: { title: 'Profile' } },
    { name: '(private)/checkups', component: Checkups, options: { title: 'Checkups' } },
    { name: 'contact', component: Contact, options: { title: 'Contact' } },
    { name: 'about', component: About, options: { title: 'About' } },
]

export default function PrivateNavigator() {
    const dimensions = useWindowDimensions();
    const isDesktop = dimensions.width >= 768;

  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
        <PrivateDrawer.Navigator
            drawerContent={(props) => <CustomDrawerContent {...props} />}
            screenOptions={{
                drawerType: isDesktop ? 'permanent' : 'front',
                headerShown: false,
                drawerStyle: {
                    minWidth: isDesktop ? 250 : 0,
                    width: isDesktop ? '15%' : '70%'
                }
            }}
        >
            {Screens.map((screen) => (
                <PrivateDrawer.Screen
                    key={screen.name}
                    name={screen.name}
                    component={screen.component}
                    options={screen.options}
                />
            ))}
        </PrivateDrawer.Navigator>
    </GestureHandlerRootView>
  );
}


/*
export default function PrivateNavigator() {
    return (
        <GestureHandlerRootView style={{ flex: 1 }}>
            <Drawer>
                <Drawer.Screen 
                    name="(private)/dashboard" 
                    options={{ title: 'Dashboard' }}
                />
                <Drawer.Screen 
                    name="(private)/settings" 
                    options={{ title: 'Settings' }}
                />
                <Drawer.Screen 
                    name="(private)/profile" 
                    options={{ title: 'Profile' }}
                />
                <Drawer.Screen 
                    name="(private)/checkups" 
                    options={{ title: 'Checkups' }}
                />
            </Drawer>
        </GestureHandlerRootView>
    )
    
}
*/