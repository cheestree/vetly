import { useAuth } from "@/hooks/useAuth";
import { View, Button } from "react-native";

export default function Login() {
  const { signIn } = useAuth();

  return (
    <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
      <Button title="Sign In with Google" onPress={signIn} />
    </View>
  );
}
