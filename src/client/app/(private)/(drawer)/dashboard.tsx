import React from "react";
import { View, Text } from "react-native";
import { usePageTitle } from "@/hooks/usePageTitle";

export default function Dashboard() {
  usePageTitle("Dashboard");

  return (
    <>
      <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
        <Text>Dashboard</Text>
      </View>
    </>
  );
}
