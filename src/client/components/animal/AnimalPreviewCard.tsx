import { useRouter } from "expo-router";
import React from "react";
import { Text, Image, StyleSheet, Pressable } from "react-native";

export default function AnimalPreviewCard({
  animal,
}: {
  animal: AnimalPreview;
}) {
  const router = useRouter();
  return (
    <Pressable
      onPress={() => router.push(`/animal/${animal.id}`)}
      style={{
        padding: 16,
        backgroundColor: "#f0f0f0",
        marginBottom: 8,
        borderRadius: 8,
      }}
    >
      <Image
        source={{ uri: animal.imageUrl }}
        style={{ width: 200, height: 200, borderRadius: 8 }}
        resizeMode="cover"
      />
      <Text>{animal.name}</Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 16,
  },
  card: {
    backgroundColor: "#fff",
    padding: 16,
    marginVertical: 8,
    borderRadius: 8,
    shadowColor: "#000",
    shadowOpacity: 0.1,
    shadowRadius: 5,
    elevation: 3, // For Android shadow
  },
  cardTitle: {
    fontSize: 18,
    fontWeight: "bold",
    marginBottom: 4,
  },
  cardId: {
    fontSize: 14,
    color: "#888",
  },
});
