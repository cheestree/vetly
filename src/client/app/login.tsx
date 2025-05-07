import { useAuth } from "@/hooks/AuthContext"
import { View, Button, Text } from "react-native"

export default function Login() {
    const { signIn, user } = useAuth()
  
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <Button title="Sign In with Google" onPress={signIn} />
      </View>
    )
  }