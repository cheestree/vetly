import CustomButton from "@/components/basic/custom/CustomButton";
import CustomTextInput from "@/components/basic/custom/CustomTextInput";
import { useAuth } from "@/hooks/useAuth";
import { useState } from "react";

export default function UserSignInContent({
  onSuccess,
}: {
  onSuccess?: () => void;
}) {
  const { signInWithEmail, signIn } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const handleSignIn = async () => {
    await signInWithEmail(email, password);
    if (onSuccess) onSuccess();
  };

  return (
    <>
      <CustomTextInput
        textLabel="Email"
        value={email}
        onChangeText={setEmail}
        keyboardType="email-address"
        autoCapitalize="none"
      />
      <CustomTextInput
        textLabel="Password"
        value={password}
        onChangeText={setPassword}
        secureTextEntry
      />
      <CustomButton onPress={handleSignIn} text="Login with Email" />
      <CustomButton
        onPress={async () => {
          await signIn();
          if (onSuccess) onSuccess();
        }}
        text="Login with Google"
      />
    </>
  );
}
