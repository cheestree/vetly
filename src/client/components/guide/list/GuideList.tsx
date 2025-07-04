import { GuidePreview } from "@/api/guide/guide.output";
import size from "@/theme/size";
import { ScrollView, StyleSheet } from "react-native";
import GuidePreviewCard from "../GuidePreviewCard";

type GuideListProps = {
  guides: GuidePreview[] | undefined;
};

export default function GuideList({ guides }: GuideListProps) {
  return (
    <ScrollView
      contentContainerStyle={extras.gridContainer}
      showsVerticalScrollIndicator={false}
    >
      {guides?.map((guide) => (
        <GuidePreviewCard key={guide.id.toString()} guide={guide} />
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
