import { SupplyPreview } from "@/api/supply/supply.output";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { ScrollView } from "react-native";
import SupplyPreviewCard from "../SupplyPreviewCard";

type SupplyListProps = {
  supplies: SupplyPreview[] | undefined;
};

export default function SupplyList({ supplies }: SupplyListProps) {
  const { styles } = useThemedStyles();

  return (
    <ScrollView contentContainerStyle={styles.listContainer}>
      {supplies?.map((supply) => <SupplyPreviewCard supply={supply} />)}
    </ScrollView>
  );
}
