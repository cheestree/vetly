import size from "@/theme/size";
import { FlatList, StyleSheet, useWindowDimensions } from "react-native";
import GuidePreviewCard from "../GuidePreviewCard";

type GuideListProps = {
  guides: GuidePreview[];
};

export default function GuideList({ guides }: GuideListProps) {
  const { width } = useWindowDimensions();

  const minColumnWidth = 200;
  const containerPadding = size.padding.xs;
  const availableWidth = width - containerPadding * 2;
  const numColumns = Math.floor(availableWidth / minColumnWidth) || 1;

  return (
    <FlatList
      data={guides}
      key={`grid-${numColumns}`}
      keyExtractor={(item) => item.id.toString()}
      numColumns={numColumns}
      renderItem={({ item }) => <GuidePreviewCard guide={item} />}
      contentContainerStyle={extra.gridContainer}
      columnWrapperStyle={numColumns > 1 ? extra.rowSpacing : undefined}
      showsVerticalScrollIndicator={false}
    />
  );
}

const extra = StyleSheet.create({
  gridContainer: {
    padding: size.padding.xs,
    gap: size.gap.md,
  },
  rowSpacing: {
    gap: size.gap.md,
  },
});
