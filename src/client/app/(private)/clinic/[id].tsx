import BaseComponent from "@/components/basic/BaseComponent";
import { useLocalSearchParams, Stack } from "expo-router";
import React, { useState, useEffect } from "react";

export default function ClinicDetails() {
  const { id } = useLocalSearchParams();
  const [clinic, setClinic] = useState<ClinicInformation>();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchClinic = async () => {
      try {
        const result = await ClinicServices.getClinic(id[0]);
        result ? setCheckup(result) : console.error("No clinic found");
      } catch (err) {
        console.error("Fetch error:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchClinic();
  }, [id]);

  return (
    <>
      <Stack.Screen options={{ title: "Clinic " + clinic?.id }} />
      <BaseComponent isLoading={loading} title={"Clinic " + clinic?.id}>
        <ClinicDetailsContent clinic={clinic} />
      </BaseComponent>
    </>
  );
}
