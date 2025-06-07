import BaseComponent from "@/components/basic/BaseComponent";
import React from "react";
import { Text } from "react-native";

export default function InventoryScreen() {
  return (
    <>
      <BaseComponent isLoading={false} title={"Inventory"}>
        <Text>Inventory</Text>
      </BaseComponent>
    </>
  );
}
