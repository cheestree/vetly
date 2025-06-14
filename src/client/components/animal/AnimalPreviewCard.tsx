import ROUTES from "@/lib/routes";
import { splitDateTime } from "@/lib/utils";
import { useRouter } from "expo-router";
import React from "react";
import { Image, Pressable, StyleSheet, Text, View } from "react-native";

export default function AnimalPreviewCard({
  animal,
}: {
  animal: AnimalPreview;
}) {
  const router = useRouter();
  const { dateOnly, timeOnly } = animal.birthDate
    ? splitDateTime(animal.birthDate)
    : { date: "", time: "" };

  return (
    <Pressable
      onPress={() =>
        router.navigate({
          pathname: ROUTES.PRIVATE.ANIMAL.DETAILS,
          params: { id: animal.id },
        })
      }
      style={extras.card}
    >
      <Image
        source={
          animal.imageUrl
            ? { uri: animal.imageUrl }
            : require("@/assets/placeholder.png") // Optional: fallback image
        }
        style={extras.image}
        resizeMode="cover"
      />

      <View style={extras.textContainer}>
        <Text style={extras.name}>{animal.name}</Text>
        <Text style={extras.meta}>
          Born on: {dateOnly ? dateOnly.toLocaleDateString() : "Unknown"} - Age{" "}
          {animal.age}
        </Text>
        {animal.species && (
          <Text style={extras.meta}>Species: {animal.species}</Text>
        )}
        {animal.owner && (
          <Text style={extras.meta}>Owner: {animal.owner.name}</Text>
        )}
      </View>
    </Pressable>
  );
}

const extras = StyleSheet.create({
  card: {
    backgroundColor: "#fff",
    height: 328,
    padding: 16,
    marginBottom: 12,
    borderRadius: 10,
    boxShadow: "0px 2px 6px rgba(0, 0, 0, 0.08)",
    elevation: 3,
  },
  image: {
    width: "100%",
    height: 200,
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
});
