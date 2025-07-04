import { CheckupPreview } from "@/api/checkup/checkup.output";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { ScrollView } from "react-native";
import CheckupPreviewCard from "../CheckupPreviewCard";

type CheckupListProps = {
  checkups: CheckupPreview[] | undefined;
};

export default function CheckupList({ checkups }: CheckupListProps) {
  const { styles } = useThemedStyles();

  return (
    <ScrollView
      contentContainerStyle={styles.gridContainer}
      showsVerticalScrollIndicator={false}
    >
      {checkups?.map((checkup) => (
        <CheckupPreviewCard key={checkup.id.toString()} checkup={checkup} />
      ))}
    </ScrollView>
  );
}
