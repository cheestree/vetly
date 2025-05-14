import { Link, useRouter } from "expo-router";
import { View, Text, StyleSheet, Pressable } from "react-native";

interface CheckupPreviewCardProps {
  checkup: CheckupPreview;
}

export default function CheckupPreviewCard({
  checkup,
}: CheckupPreviewCardProps) {
  const router = useRouter();
  return (
    <View style={styles.card}>
      <Text style={styles.title}></Text>
      <Text>ID: {checkup.id}</Text>
      <Text>Date: {new Date(checkup.dateTime).toLocaleString()}</Text>
      <Text>Status: {checkup.status}</Text>
      <Text>Animal: {checkup.animal.name}</Text>
      <Text>Veterinarian: {checkup.veterinarian.name}</Text>
      <Text>Clinic: {checkup.clinic.name}</Text>
      <Text>Description: {checkup.description}</Text>
      <Pressable
        onPress={() => router.push(`/checkup/${checkup.id}`)}
        style={{
          padding: 16,
          backgroundColor: "#f0f0f0",
          marginBottom: 8,
          borderRadius: 8,
        }}
      >
        <Text>{checkup.dateTime}</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    padding: 16,
    backgroundColor: "#fff",
    borderRadius: 10,
    marginBottom: 12,
    shadowColor: "#000",
    shadowOpacity: 0.1,
    shadowRadius: 6,
    elevation: 2,
  },
  title: {
    fontSize: 18,
    fontWeight: "bold",
    marginBottom: 4,
  },
  description: {
    fontSize: 14,
    color: "#666",
    marginBottom: 8,
  },
});
