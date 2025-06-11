import { useWindowDimensions, StyleSheet } from "react-native";
import CheckupPreviewCard from "../CheckupPreviewCard";
import size from "@/theme/size";
import { FlatList } from "react-native-gesture-handler";

type CheckupListProps = {
  checkups: CheckupPreview[];
};

export default function CheckupList({ checkups }: CheckupListProps) {
  const { width } = useWindowDimensions();

  const minColumnWidth = 200;
  const containerPadding = size.padding.xs;
  const availableWidth = width - containerPadding * 2;
  const numColumns = Math.floor(availableWidth / minColumnWidth) || 1;

  return (
    <FlatList
      data={checkups}
      key={`grid-${numColumns}`}
      keyExtractor={(item) => item.id.toString()}
      numColumns={numColumns}
      renderItem={({ item }) => <CheckupPreviewCard checkup={item} />}
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
