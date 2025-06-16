import BaseComponent from "@/components/basic/base/BaseComponent";
import React from "react";
import { Text } from "react-native";

//  Specific inventory item
export default function InventoryItemScreen() {
  return (
    <>
      <BaseComponent isLoading={false} title={"Search Inventories"}>
        <Text>Search Inventories</Text>
      </BaseComponent>
    </>
  );
}
