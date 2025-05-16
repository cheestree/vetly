import { useLocalSearchParams } from 'expo-router'
import { Text, View } from 'react-native'

export default function ErrorScreen() {
  const { code, message } = useLocalSearchParams()

  return (
    <View>
      <Text>Error {code}</Text>
      {message && <Text>{message}</Text>}
    </View>
  )
}