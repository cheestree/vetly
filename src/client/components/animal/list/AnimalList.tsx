import { AnimalPreview } from "@/api/animal/animal.output";
import size from "@/theme/size";
import { FlatList, StyleSheet } from "react-native";
import AnimalPreviewCard from "../AnimalPreviewCard";

type AnimalListProps = {
  animals: AnimalPreview[];
};

export default function AnimalList({ animals }: AnimalListProps) {
  return (
    <FlatList
      data={animals}
      keyExtractor={(item) => item.id.toString()}
      renderItem={({ item }) => <AnimalPreviewCard animal={item} />}
      contentContainerStyle={extras.gridContainer}
      showsVerticalScrollIndicator={false}
    />
  );
}

const extras = StyleSheet.create({
  gridContainer: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: size.gap.xl,
  },
  rowSpacing: {
    gap: size.gap.xl,
  },
});
