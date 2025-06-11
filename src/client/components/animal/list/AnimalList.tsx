import { useWindowDimensions, StyleSheet } from "react-native";
import size from "@/theme/size";
import AnimalPreviewCard from "../AnimalPreviewCard";
import { FlatList } from "react-native-gesture-handler";

type AnimalListProps = {
  animals: AnimalPreview[];
};

export default function AnimalList({ animals }: AnimalListProps) {
  const { width } = useWindowDimensions();

  const minColumnWidth = 200;
  const containerPadding = size.padding.xs;
  const availableWidth = width - containerPadding * 2;
  const numColumns = Math.floor(availableWidth / minColumnWidth) || 1;

  return (
    <FlatList
      data={animals}
      key={`grid-${numColumns}`}
      keyExtractor={(item) => item.id.toString()}
      numColumns={numColumns}
      renderItem={({ item }) => <AnimalPreviewCard animal={item} />}
      contentContainerStyle={styles.gridContainer}
      columnWrapperStyle={numColumns > 1 ? styles.rowSpacing : undefined}
      showsVerticalScrollIndicator={false}
    />
  );
}

const styles = StyleSheet.create({
  gridContainer: {
    padding: size.padding.xs,
    gap: size.gap.md,
  },
  rowSpacing: {
    gap: size.gap.md,
  },
});
