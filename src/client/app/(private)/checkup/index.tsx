import React from "react";
import { View, Text } from "react-native";
import Title from "expo-router/head";
import CheckupSearchScreen from "@/components/checkup/CheckupSearchScreen";

export default function Checkups() {
  return (
    <>
      <Title>Checkups</Title>
      <CheckupSearchScreen/>
    </>
  );
}
