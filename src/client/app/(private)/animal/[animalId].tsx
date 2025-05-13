import { useLocalSearchParams } from "expo-router";
import AnimalServices from "@/api/services/AnimalServices";
import AnimalDetailsContent from "@/components/animal/AnimalDetailsContent";
import BaseComponent from "@/components/BaseComponent";
import { useAuth } from "@/hooks/AuthContext";
import { usePageTitle } from "@/hooks/usePageTitle";
import { useEffect, useState } from "react";

export default function AnimalDetails() {
  const { animalId } = useLocalSearchParams();
  const { token, loading: authLoading } = useAuth();
  const [animal, setAnimal] = useState<AnimalInformation>();
  const [loading, setLoading] = useState(true);
  usePageTitle("Animal " + animal?.id);

  useEffect(() => {
    const fetchAnimal = async () => {
      if (!token) {
        console.error("User is not authenticated");
        setLoading(false);
        return;
      }

      try {
        const result = await AnimalServices.getAnimal(animalId[0], token);
        result ? setAnimal(result) : console.error("No animal found");
      } catch (err) {
        console.error("Fetch error:", err);
      } finally {
        setLoading(false);
      }
    };

    if (!authLoading) fetchAnimal();
  }, [animalId, token, authLoading]);

  return (
    <BaseComponent isLoading={authLoading || loading}>
      <AnimalDetailsContent animal={animal} />
    </BaseComponent>
  );
}
