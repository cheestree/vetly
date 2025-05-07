import { useAuth } from '@/hooks/AuthContext'
import { DrawerContentScrollView, DrawerItemList, DrawerItem, DrawerContentComponentProps } from '@react-navigation/drawer'
import React, { useEffect, useState } from 'react'
import { View, Text, StyleSheet, Platform } from 'react-native'

export default function CustomDrawerContent(props: DrawerContentComponentProps) {
    const { user, signOut } = useAuth()

	useEffect(() => {
		if (Platform.OS === 'web') {
			const route = props.state.routes[props.state.index]
			const title = props.descriptors[route.key]?.options?.title || route.name
			document.title = title
		}
	  }, [props.state.index])
	  
  return (
    <DrawerContentScrollView {...props}>
		<View style={{ padding: 16 }}>
			<Text style={{ fontSize: 18 }}>👋 Welcome, {user?.displayName}</Text>
		</View>

		{props.state.routes.map((route, index) => {
			const isFocused = props.state.index === index
			const options = props.descriptors[route.key].options

			return (
				<DrawerItem
				key={route.key}
				label={options.title || route.name}
				onPress={() => props.navigation.navigate(route.name)}
				icon={options.drawerIcon}
				focused={isFocused}
				activeTintColor='#6200ee'
				inactiveTintColor='#333'
				activeBackgroundColor='#e0ddff'
				style={{
					marginHorizontal: 8,
					marginVertical: 4,
					borderRadius: 8,
				}}
				/>
			)
		})}
    </DrawerContentScrollView>
  )
}