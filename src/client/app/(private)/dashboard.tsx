import BaseComponent from "@/components/basic/BaseComponent";
import React from "react";
import { Text } from "react-native";

export default function DashboardScreen() {
  return (
    <>
      <BaseComponent isLoading={false} title={"Dashboard"}>
        <Text>Dashboard</Text>
      </BaseComponent>
    </>
  );
}
