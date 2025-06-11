import { useWindowDimensions, StyleSheet } from "react-native";
import size from "@/theme/size";
import { FlatList } from "react-native-gesture-handler";
import ClinicPreviewCard from "../ClinicPreviewCard";

type ClinicListProps = {
  clinics: ClinicPreview[];
};

export default function ClinicList({ clinics }: ClinicListProps) {
  const { width } = useWindowDimensions();

  const minColumnWidth = 200;
  const containerPadding = size.padding.xs;
  const availableWidth = width - containerPadding * 2;
  const numColumns = Math.floor(availableWidth / minColumnWidth) || 1;

  return (
    <FlatList
      data={clinics}
      key={`grid-${numColumns}`}
      keyExtractor={(item) => item.id.toString()}
      numColumns={numColumns}
      renderItem={({ item }) => <ClinicPreviewCard clinic={item} />}
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
