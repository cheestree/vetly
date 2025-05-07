import { BottomTabBarProps } from "@react-navigation/bottom-tabs"
import { useEffect } from "react"
import { Platform, View, Pressable, Text } from "react-native"

export default function CustomBottomTabBar(props: BottomTabBarProps) {
	const { state, descriptors, navigation } = props

	useEffect(() => {
		if (Platform.OS === 'web') {
			const route = state.routes[state.index]
			const title = descriptors[route.key]?.options?.title || route.name
			document.title = title
		}
	}, [state.index])

	return (
		<View
			style={{
				flexDirection: 'row',
				justifyContent: 'space-around',
				paddingVertical: 12,
				borderTopWidth: 1,
				borderTopColor: '#ddd',
				backgroundColor: '#fff'
			}}
		>
			{state.routes.map((route, index) => {
				const isFocused = state.index === index
				const { options } = descriptors[route.key]
				const label = options.title || route.name
				const icon = options.tabBarIcon

				const color = isFocused ? '#6200ee' : '#333'

				return (
					<Pressable
						key={route.key}
						onPress={() => navigation.navigate(route.name)}
						style={{
							alignItems: 'center',
							flex: 1,
							padding: 8,
							backgroundColor: isFocused ? '#e0ddff' : 'transparent',
                            marginHorizontal: 4,
                            marginVertical: 2,
                            borderRadius: 4,
						}}
					>
						{icon?.({ color, size: 20, focused: isFocused })}
						<Text style={{ color, fontSize: 12 }}>{label}</Text>
					</Pressable>
				)
			})}
		</View>
	)
}