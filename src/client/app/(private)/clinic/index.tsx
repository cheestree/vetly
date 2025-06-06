import BaseComponent from "@/components/BaseComponent";
import React from "react";
import { Text } from "react-native";

export default function ClinicSearchScreen() {
  return (
    <>
      <BaseComponent isLoading={false} title={"Search Clinics"}>
        <Text>Search Clinics</Text>
      </BaseComponent>
    </>
  );
}
