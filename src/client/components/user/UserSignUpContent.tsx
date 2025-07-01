import CustomButton from "@/components/basic/custom/CustomButton";
import CustomTextInput from "@/components/basic/custom/CustomTextInput";
import { useAuth } from "@/hooks/useAuth";
import { useState } from "react";

export default function UserSignUpContent({
  onSuccess,
}: {
  onSuccess?: () => void;
}) {
  const { signUpWithEmail } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const handleSignUp = async () => {
    await signUpWithEmail(email, password);
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
      <CustomButton onPress={handleSignUp} text="Sign Up with Email" />
    </>
  );
}
