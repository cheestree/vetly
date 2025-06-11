import { formatOpeningHours } from "@/lib/utils";
import { View, Text, ScrollView, StyleSheet, Image } from "react-native";

export default function ClinicDetailsContent({
  clinic,
}: {
  clinic?: ClinicInformation
}) {
  if (!clinic) {
    return (
      <View style={styles.container}>
        <Text style={styles.placeholder}>No clinic data found.</Text>
      </View>
    )
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

      <Text style={styles.heading}>{clinic.name}</Text>
      <Text style={styles.field}>📍 {clinic.address}</Text>
      <Text style={styles.field}>📞 {clinic.phone}</Text>
      <Text style={styles.field}>✉️ {clinic.email}</Text>

      {clinic.owner && (
        <Text style={styles.field}>
          Owner: {clinic.owner.name} ({clinic.owner.email})
        </Text>
      )}

      <Text style={[styles.field, styles.sectionHeading]}>
        Opening Hours:
      </Text>
      
      <Text style={styles.openingHours}>
        {formatOpeningHours(clinic.openingHours)}
      </Text>

      <Text style={[styles.field, styles.sectionHeading]}>Services:</Text>
      <Text style={styles.services}>
        {clinic.services.length > 0 ? clinic.services.join(", ") : "No services listed"}
      </Text>
    </ScrollView>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
  },
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
})