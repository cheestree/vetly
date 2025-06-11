import ROUTES from "@/lib/routes";
import { useRouter } from "expo-router";
import { View, StyleSheet, Pressable, Image, Text } from "react-native";

interface ClinicPreviewCardProps {
  clinic: ClinicPreview;
}

export default function ClinicPreviewCard({ clinic }: ClinicPreviewCardProps) {
  const router = useRouter()

  return (
    <Pressable
      onPress={() =>
        router.navigate({
          pathname: ROUTES.PRIVATE.CLINIC.DETAILS,
          params: { id: clinic.id },
        })
      }
      style={styles.card}
    >
      <Image
        source={
          clinic.imageUrl
            ? { uri: clinic.imageUrl }
            : require("@/assets/placeholder.png")
        }
        style={styles.image}
        resizeMode="cover"
      />

      <View style={styles.textContainer}>
        <Text style={styles.name}>{clinic.name}</Text>
        <Text style={styles.meta}>📍 {clinic.address}</Text>
        <Text style={styles.meta}>📞 {clinic.phone}</Text>
        <Text style={styles.meta}>
          Services: {clinic.services.length > 0 ? clinic.services.join(", ") : "None listed"}
        </Text>
      </View>
    </Pressable>
  )
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: "#fff",
    height: 300,
    padding: 16,
    marginBottom: 12,
    borderRadius: 10,
    shadowColor: "#000",
    shadowOpacity: 0.08,
    shadowRadius: 6,
    shadowOffset: { width: 0, height: 2 },
    elevation: 3,
  },
  image: {
    width: "100%",
    height: 180,
    borderRadius: 8,
    backgroundColor: "#e0e0e0",
  },
  textContainer: {
    marginTop: 12,
  },
  name: {
    fontSize: 18,
    fontWeight: "bold",
  },
  meta: {
    fontSize: 14,
    color: "#666",
    marginTop: 2,
  },
})