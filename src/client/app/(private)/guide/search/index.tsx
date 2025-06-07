import BaseComponent from "@/components/basic/BaseComponent";
import React from "react";
import { Text } from "react-native";

export default function GuideSearchScreen() {
  return (
    <>
      <BaseComponent isLoading={false} title={"Search Guides"}>
        <Text>Search Guides</Text>
      </BaseComponent>
    </>
  );
}
