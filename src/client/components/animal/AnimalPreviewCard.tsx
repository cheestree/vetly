import { AnimalPreview } from "@/api/animal/animal.output";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import ROUTES from "@/lib/routes";
import { splitDateTime } from "@/lib/utils";
import { useRouter } from "expo-router";
import React from "react";
import { Pressable, View } from "react-native";
import SafeImage from "../basic/SafeImage";
import CustomButton from "../basic/custom/CustomButton";
import CustomText from "../basic/custom/CustomText";

export default function AnimalPreviewCard({
  animal,
}: {
  animal: AnimalPreview;
}) {
  const { styles } = useThemedStyles();
  const router = useRouter();
  const { dateOnly } = animal.birthDate
    ? splitDateTime(animal.birthDate)
    : { dateOnly: "", timeOnly: { hours: "", minutes: "", seconds: "" } };

  return (
    <Pressable
      onPress={() =>
        router.navigate({
          pathname: ROUTES.PRIVATE.ANIMAL.DETAILS,
          params: { id: animal.id },
        })
      }
      style={styles.cardContainer}
    >
      <SafeImage uri={animal.image?.url} alt="Animal Image" />
      <View style={styles.cardInfoContainer}>
        <CustomText text={`${animal.name}`} />
        <CustomText
          text={`Born on: ${dateOnly ? dateOnly : "Unknown"} - Age ${animal.age}`}
        />
        {animal.species && <CustomText text={`Species: ${animal.species}`} />}
        <CustomButton
          onPress={() => {
            if (animal.owner) {
              router.navigate({
                pathname: ROUTES.PRIVATE.USER.DETAILS,
                params: { id: animal.owner.id },
              });
            }
          }}
          disabled={!animal.owner}
          text={`Owner: ${animal.owner?.name ?? "—"}`}
        />
      </View>
    </Pressable>
  );
}
