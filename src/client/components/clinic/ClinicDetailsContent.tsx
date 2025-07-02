import { ClinicInformation } from "@/api/clinic/clinic.output";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { formatOpeningHours } from "@/lib/utils";
import size from "@/theme/size";
import { Platform, Text, useWindowDimensions, View } from "react-native";
import SafeImage from "../basic/SafeImage";

export default function ClinicDetailsContent({
  clinic,
}: {
  clinic?: ClinicInformation;
}) {
  const { width } = useWindowDimensions();
  const { styles } = useThemedStyles();
  const isDesktop = Platform.OS === "web" && width >= 768;

  if (!clinic) {
    return (
      <View style={styles.container}>
        <Text style={styles.info}>No clinic data found.</Text>
      </View>
    );
  }

  return (
    <View
      style={[
        styles.container,
        {
          flexDirection: isDesktop ? "row" : "column",
          gap: size.gap.md,
        },
      ]}
    >
      <SafeImage
        uri={clinic.imageUrl}
        fallback={require("@/assets/placeholder.png")}
        style={styles.image}
        resizeMode="cover"
        alt="Clinic Image"
      />

      <View>
        <Text style={styles.header}>{clinic.name}</Text>
        <Text style={styles.info}>📍 {clinic.address}</Text>
        <Text style={styles.info}>📞 {clinic.phone}</Text>
        <Text style={styles.info}>✉️ {clinic.email}</Text>

        {clinic.owner && (
          <Text style={styles.info}>
            Owner {clinic.owner.name} ({clinic.owner.email})
          </Text>
        )}

        <Text style={styles.header}>Opening Hours</Text>

        <Text style={styles.info}>
          {formatOpeningHours(clinic.openingHours)}
        </Text>

        <Text style={styles.header}>Services:</Text>
        <Text style={styles.info}>
          {clinic.services.length > 0
            ? clinic.services.join(", ")
            : "No services listed"}
        </Text>
      </View>
    </View>
  );
}
