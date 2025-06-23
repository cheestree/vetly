import BaseComponent from "@/components/basic/base/BaseComponent";
import { useAuth } from "@/hooks/useAuth";
import { Text } from "react-native";

export default function ProfileScreen() {
  const { information } = useAuth();

  return (
    <BaseComponent title={"Profile"}>
      <Text>Profile</Text>
      <Text>{information?.name}</Text>
    </BaseComponent>
  );
}
