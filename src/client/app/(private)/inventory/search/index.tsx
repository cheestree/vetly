import BaseComponent from "@/components/basic/BaseComponent";
import React from "react";
import { Text } from "react-native";

export default function InventorySearchScreen() {
  return (
    <>
      <BaseComponent isLoading={false} title={"Search Inventories"}>
        <Text>Search Inventories</Text>
      </BaseComponent>
    </>
  );
}
