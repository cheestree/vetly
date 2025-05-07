import { View, Text } from "react-native";
import Title from 'expo-router/head'

export default function About() {
  return (
    <>
      <Title>About</Title>
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <Text>About Page (Stack pushed)</Text>
      </View>
    </>
  )
}