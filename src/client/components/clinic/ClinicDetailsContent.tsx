import { useThemedStyles } from "@/hooks/useThemedStyles";
import { formatOpeningHours } from "@/lib/utils";
import { Image, ScrollView, StyleSheet, Text, View } from "react-native";

export default function ClinicDetailsContent({
  clinic,
}: {
  clinic?: ClinicInformation;
}) {
  const { colours, styles } = useThemedStyles();

  if (!clinic) {
    return (
      <View style={styles.container}>
        <Text style={extras.placeholder}>No clinic data found.</Text>
      </View>
    );
  }

  return (
    <ScrollView style={styles.container}>
      {clinic.imageUrl && (
        <Image
          source={{ uri: clinic.imageUrl }}
          style={styles.image}
          resizeMode="cover"
        />
      )}

      <Text style={extras.heading}>{clinic.name}</Text>
      <Text style={extras.field}>📍 {clinic.address}</Text>
      <Text style={extras.field}>📞 {clinic.phone}</Text>
      <Text style={extras.field}>✉️ {clinic.email}</Text>

      {clinic.owner && (
        <Text style={extras.field}>
          Owner: {clinic.owner.name} ({clinic.owner.email})
        </Text>
      )}

      <Text style={[extras.field, extras.sectionHeading]}>Opening Hours:</Text>

      <Text style={extras.openingHours}>
        {formatOpeningHours(clinic.openingHours)}
      </Text>

      <Text style={[extras.field, extras.sectionHeading]}>Services:</Text>
      <Text style={extras.services}>
        {clinic.services.length > 0
          ? clinic.services.join(", ")
          : "No services listed"}
      </Text>
    </ScrollView>
  );
}

const extras = StyleSheet.create({
  placeholder: {
    fontSize: 16,
    color: "#999",
    textAlign: "center",
  },
  heading: {
    fontSize: 24,
    marginVertical: 16,
    fontWeight: "bold",
  },
  field: {
    fontSize: 16,
    marginBottom: 8,
  },
  image: {
    width: "100%",
    height: 200,
    borderRadius: 8,
  },
  sectionHeading: {
    fontWeight: "bold",
    marginTop: 16,
    marginBottom: 4,
  },
  openingHours: {
    fontSize: 14,
    color: "#444",
    lineHeight: 20,
  },
  services: {
    fontSize: 14,
    color: "#444",
  },
});
