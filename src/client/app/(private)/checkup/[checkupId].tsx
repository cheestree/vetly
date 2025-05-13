import { useLocalSearchParams } from "expo-router";
import React, { useEffect, useState } from "react";
import CheckupServices from "@/api/services/CheckupServices";
import { useAuth } from "@/hooks/AuthContext";
import BaseComponent from "@/components/BaseComponent";
import CheckupDetailsContent from "@/components/checkup/CheckupDetailsContent";
import { usePageTitle } from "@/hooks/usePageTitle";

export default function CheckupDetails() {
  const { checkupId } = useLocalSearchParams();
  const { token, loading: authLoading } = useAuth();
  const [checkup, setCheckup] = useState<CheckupInformation>();
  const [loading, setLoading] = useState(true);
  usePageTitle("Checkup " + checkup?.id);

  useEffect(() => {
    const fetchCheckup = async () => {
      if (!token) {
        console.error("User is not authenticated");
        setLoading(false);
        return;
      }

      try {
        const result = await CheckupServices.getCheckup(checkupId[0], token);
        result ? setCheckup(result) : console.error("No checkup found");
      } catch (err) {
        console.error("Fetch error:", err);
      } finally {
        setLoading(false);
      }
    };

    if (!authLoading) fetchCheckup();
  }, [checkupId, token, authLoading]);

  return (
    <BaseComponent isLoading={authLoading || loading}>
      <CheckupDetailsContent checkup={checkup} />
    </BaseComponent>
  );
}
