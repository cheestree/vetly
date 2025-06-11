import { useLocalSearchParams } from "expo-router";
import { SafeAreaView, Text } from "react-native";

export default function ErrorScreen() {
  const { code, message } = useLocalSearchParams();

  return (
    <SafeAreaView>
      <Text>Error {code}</Text>
      {message && <Text>{message}</Text>}
    </SafeAreaView>
  );
}
