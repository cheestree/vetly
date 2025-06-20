import { CheckupPreview } from "@/api/checkup/checkup.output";
import size from "@/theme/size";
import { ScrollView, StyleSheet } from "react-native";
import CheckupPreviewCard from "../CheckupPreviewCard";

type CheckupListProps = {
  checkups: CheckupPreview[];
};

export default function CheckupList({ checkups }: CheckupListProps) {
  return (
    <ScrollView
      contentContainerStyle={extras.gridContainer}
      showsVerticalScrollIndicator={false}
    >
      {checkups.map((checkup) => (
        <CheckupPreviewCard key={checkup.id.toString()} checkup={checkup} />
      ))}
    </ScrollView>
  );
}

const extras = StyleSheet.create({
  gridContainer: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: size.gap.xl,
  },
});
