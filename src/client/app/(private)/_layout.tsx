import CustomDrawerContent from '@/components/drawer/CustomDrawerContent';
import { FontAwesome } from '@expo/vector-icons';
import { Drawer } from 'expo-router/drawer';
import { useWindowDimensions } from 'react-native';

export default function PrivateLayout() {
  const dimensions = useWindowDimensions();
  const isDesktop = dimensions.width >= 768;

  return (
    <Drawer
      drawerContent={(props) => <CustomDrawerContent {...props} />}
      screenOptions={{
        drawerType: isDesktop ? "permanent" : "front",
        headerShown: false,
        drawerStyle: {
          minWidth: isDesktop ? 250 : 0,
          width: isDesktop ? "15%" : "70%",
        }
      }}
    >
      <Drawer.Screen
        name='dashboard'
        options={{
          title: 'Dashboard',
          drawerLabel: 'Dashboard',
          drawerIcon: () => <FontAwesome name='home' size={20}/>
        }}
      />
      <Drawer.Screen
        name='profile'
        options={{
          title: 'Profile',
          drawerLabel: 'Profile',
          drawerIcon: () => <FontAwesome name='user' size={20}/>
        }}
      />
      <Drawer.Screen
        name='settings'
        options={{
          title: 'Settings',
          drawerLabel: 'Settings',
          drawerIcon: () => <FontAwesome name='gear' size={20}/>
        }}
      />
      <Drawer.Screen
        name='checkup'
        options={{
          title: 'Checkups',
          drawerLabel: 'Search Checkups',
          drawerIcon: () => <FontAwesome name='search' size={20}/>
        }}
      />
    </Drawer>
  );
}