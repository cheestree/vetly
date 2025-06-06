import { Stack, useLocalSearchParams } from "expo-router";
import React, { useEffect, useState } from "react";
import CheckupServices from "@/api/services/CheckupServices";
import BaseComponent from "@/components/BaseComponent";
import CheckupDetailsContent from "@/components/checkup/CheckupDetailsContent";

export default function CheckupDetails() {
  const { id } = useLocalSearchParams();
  const [checkup, setCheckup] = useState<CheckupInformation>();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchCheckup = async () => {
      try {
        const result = await CheckupServices.getCheckup(id[0]);
        result ? setCheckup(result) : console.error("No checkup found");
      } catch (err) {
        console.error("Fetch error:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchCheckup();
  }, [id]);

  return (
    <>
      <Stack.Screen options={{ title: "Checkup " + checkup?.id }} />
      <BaseComponent isLoading={loading} title={"Checkup " + checkup?.id}>
        <CheckupDetailsContent checkup={checkup} />
      </BaseComponent>
    </>
  );
}
