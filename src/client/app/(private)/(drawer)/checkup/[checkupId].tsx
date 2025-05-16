import { useLocalSearchParams } from "expo-router";
import React, { useEffect, useState } from "react";
import CheckupServices from "@/api/services/CheckupServices";
import { useAuth } from "@/hooks/useAuth";
import BaseComponent from "@/components/BaseComponent";
import CheckupDetailsContent from "@/components/checkup/CheckupDetailsContent";
import { usePageTitle } from "@/hooks/usePageTitle";

export default function CheckupDetails() {
  const { checkupId } = useLocalSearchParams();
  const { loading: authLoading } = useAuth();
  const [checkup, setCheckup] = useState<CheckupInformation>();
  const [loading, setLoading] = useState(true);
  usePageTitle("Checkup " + checkup?.id);

  useEffect(() => {
    const fetchCheckup = async () => {
      try {
        const result = await CheckupServices.getCheckup(checkupId[0]);
        result ? setCheckup(result) : console.error("No checkup found");
      } catch (err) {
        console.error("Fetch error:", err);
      } finally {
        setLoading(false);
      }
    };

    if (!authLoading) fetchCheckup();
  }, [checkupId, authLoading]);

  return (
    <BaseComponent isLoading={authLoading || loading}>
      <CheckupDetailsContent checkup={checkup} />
    </BaseComponent>
  );
}
