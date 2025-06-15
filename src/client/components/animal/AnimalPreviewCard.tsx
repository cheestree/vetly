import { useThemedStyles } from "@/hooks/useThemedStyles";
import ROUTES from "@/lib/routes";
import { splitDateTime } from "@/lib/utils";
import size from "@/theme/size";
import { useRouter } from "expo-router";
import React from "react";
import { Pressable, StyleSheet, View } from "react-native";
import CustomText from "../basic/CustomText";
import SafeImage from "../basic/SafeImage";

export default function AnimalPreviewCard({
  animal,
}: {
  animal: AnimalPreview;
}) {
  const { colours, styles } = useThemedStyles();
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
      style={[styles.cardContainer, extras.cardContainer]}
    >
      <View style={styles.cardImageContainer}>
        <SafeImage
          uri={animal.imageUrl}
          fallback={require("@/assets/placeholder.png")}
          style={styles.image}
          resizeMode="cover"
        />
      </View>
      <View style={styles.cardInfoContainer}>
        <CustomText text={`${animal.name}`} />
        <CustomText
          text={`Born on: ${dateOnly ? dateOnly.toLocaleDateString() : "Unknown"} - Age ${animal.age}`}
        />
        {animal.species && <CustomText text={`Species: ${animal.species}`} />}
        {animal.owner && <CustomText text={`Owner: ${animal.owner.name}`} />}
      </View>
    </Pressable>
  );
}

const extras = StyleSheet.create({
  cardContainer: {
    height: size.size.xl,
  },
});
