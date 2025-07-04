import { AnimalPreview } from "@/api/animal/animal.output";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { ScrollView } from "react-native";
import AnimalPreviewCard from "../AnimalPreviewCard";

type AnimalListProps = {
  animals: AnimalPreview[] | undefined;
};

export default function AnimalList({ animals }: AnimalListProps) {
  const { styles } = useThemedStyles();

  return (
    <ScrollView
      contentContainerStyle={styles.gridContainer}
      showsVerticalScrollIndicator={false}
    >
      {animals?.map((animal) => (
        <AnimalPreviewCard key={animal.id.toString()} animal={animal} />
      ))}
    </ScrollView>
  );
}
