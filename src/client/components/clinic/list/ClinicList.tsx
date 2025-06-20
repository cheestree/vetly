import { ClinicPreview } from "@/api/clinic/clinic.output";
import size from "@/theme/size";
import { ScrollView, StyleSheet } from "react-native";
import ClinicPreviewCard from "../ClinicPreviewCard";

type ClinicListProps = {
  clinics: ClinicPreview[];
};

export default function ClinicList({ clinics }: ClinicListProps) {
  return (
    <ScrollView
      contentContainerStyle={extras.gridContainer}
      showsVerticalScrollIndicator={false}
    >
      {clinics.map((clinic) => (
        <ClinicPreviewCard key={clinic.id.toString()} clinic={clinic} />
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
