import size from "@/theme/size";
import { FlatList, StyleSheet, useWindowDimensions } from "react-native";
import CheckupPreviewCard from "../CheckupPreviewCard";

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
      contentContainerStyle={extras.gridContainer}
      columnWrapperStyle={numColumns > 1 ? extras.rowSpacing : undefined}
      showsVerticalScrollIndicator={false}
    />
  );
}

const extras = StyleSheet.create({
  gridContainer: {
    padding: size.padding.xs,
    gap: size.gap.md,
  },
  rowSpacing: {
    gap: size.gap.md,
  },
});
