import { View, Text } from "react-native";

export default function NotFoundScreen() {
  return (
    <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
      <Text style={{ fontSize: 18 }}>404 - Page Not Found</Text>
    </View>
  )
}