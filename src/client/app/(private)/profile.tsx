import { Text } from "react-native";
import { useAuth } from "@/hooks/useAuth";
import BaseComponent from "@/components/basic/BaseComponent";

export default function ProfileScreen() {
  const { information } = useAuth();

  return (
    <>
      <BaseComponent isLoading={false} title={"Profile"}>
        <Text>Profile</Text>
        <Text>{information?.name}</Text>
      </BaseComponent>
    </>
  );
}
