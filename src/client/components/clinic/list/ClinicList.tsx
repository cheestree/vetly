import { ClinicPreview } from "@/api/clinic/clinic.output";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { ScrollView } from "react-native";
import ClinicPreviewCard from "../ClinicPreviewCard";

type ClinicListProps = {
  clinics: ClinicPreview[] | undefined;
};

export default function ClinicList({ clinics }: ClinicListProps) {
  const { styles } = useThemedStyles();

  return (
    <ScrollView
      contentContainerStyle={styles.gridContainer}
      showsVerticalScrollIndicator={false}
    >
      {clinics?.map((clinic) => (
        <ClinicPreviewCard key={clinic.id.toString()} clinic={clinic} />
      ))}
    </ScrollView>
  );
}
