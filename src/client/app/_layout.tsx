import { AuthProvider, useAuth } from '@/hooks/AuthContext'
import React from 'react';
import { ActivityIndicator, View } from 'react-native';
import PrivateNavigator from '@/components/navigators/PrivateNavigator';
import PublicNavigator from '@/components/navigators/PublicNavigator';
import { Stack } from 'expo-router';

export default function Layout() {
  return (
    <AuthProvider>
      <LayoutContent/>
    </AuthProvider>
  )
}


function LayoutContent() {
 const { user, loading } = useAuth()

 if (loading) {
    return (
      <View>
        <ActivityIndicator size="large" color="#0000ff" />
      </View>
    )
 }

  return user ? <PrivateNavigator /> : <PublicNavigator />
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