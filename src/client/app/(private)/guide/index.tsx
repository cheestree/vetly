import BaseComponent from "@/components/basic/BaseComponent";
import React from "react";
import { Text } from "react-native";

export default function GuideScreen() {
  return (
    <>
      <BaseComponent isLoading={false} title={"Guides"}>
        <Text>Guide</Text>
      </BaseComponent>
    </>
  );
}
