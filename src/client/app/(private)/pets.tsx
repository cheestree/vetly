import BaseComponent from "@/components/basic/BaseComponent";
import { Text } from "react-native";

export default function MyPetsScreen() {
  return (
    <>
      <BaseComponent isLoading={false} title={"Pets"}>
        <Text>Pets</Text>
      </BaseComponent>
    </>
  );
}
