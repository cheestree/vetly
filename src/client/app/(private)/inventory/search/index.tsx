import BaseComponent from "@/components/basic/base/BaseComponent";
import React from "react";
import { Text } from "react-native";

//  Supply search
export default function SupplySearchScreen() {
  return (
    <>
      <BaseComponent isLoading={false} title={"Search Inventories"}>
        <Text>Search Inventories</Text>
      </BaseComponent>
    </>
  );
}
