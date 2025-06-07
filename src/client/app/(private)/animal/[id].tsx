import { Stack, useLocalSearchParams } from "expo-router";
import AnimalServices from "@/api/services/AnimalServices";
import BaseComponent from "@/components/basic/BaseComponent";
import { useEffect, useState } from "react";
import AnimalDetailsContent from "@/components/animal/AnimalDetailsContent";

export default function AnimalDetails() {
  const { id } = useLocalSearchParams();
  const [animal, setAnimal] = useState<AnimalInformation>();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAnimal = async () => {
      try {
        const result = await AnimalServices.getAnimal(id[0]);
        result ? setAnimal(result) : console.error("No checkup found");
      } catch (err) {
        console.error("Fetch error:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchAnimal();
  }, [id]);

  return (
    <>
      <Stack.Screen options={{ title: animal?.name }} />
      <BaseComponent
        isLoading={loading}
        title={animal?.name || "Animal Details"}
      >
        <AnimalDetailsContent animal={animal} />
      </BaseComponent>
    </>
  );
}
