import BaseComponent from "@/components/BaseComponent";
import { useNavigation } from "expo-router";
import Drawer from "expo-router/drawer";
import React, { useLayoutEffect } from "react";
import { View, Text, SafeAreaView } from "react-native";

export default function DashboardScreen() {
  return (
    <>
      <BaseComponent isLoading={false} title={"Dashboard"}>
        <Text>Dashboard</Text>
      </BaseComponent>
    </>
  );
}
