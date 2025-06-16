import { useThemedStyles } from "@/hooks/useThemedStyles";
import ROUTES from "@/lib/routes";
import { splitDateTime } from "@/lib/utils";
import size from "@/theme/size";
import { useRouter } from "expo-router";
import { Pressable, StyleSheet, View } from "react-native";
import SafeImage from "../basic/SafeImage";
import CustomButton from "../basic/custom/CustomButton";
import CustomText from "../basic/custom/CustomText";

interface CheckupPreviewCardProps {
  checkup: CheckupPreview;
}

export default function CheckupPreviewCard({
  checkup,
}: CheckupPreviewCardProps) {
  const { styles } = useThemedStyles();
  const router = useRouter();
  const { dateOnly, timeOnly } = splitDateTime(checkup.dateTime);

  return (
    <Pressable
      onPress={() =>
        router.navigate({
          pathname: ROUTES.PRIVATE.CHECKUP.DETAILS,
          params: { id: checkup.id },
        })
      }
      style={[styles.cardContainer, extras.cardContainer]}
    >
      <View style={styles.cardImageContainer}>
        <SafeImage
          uri={checkup.animal.imageUrl}
          fallback={require("@/assets/placeholder.png")}
          style={styles.image}
          resizeMode="cover"
        />
      </View>
      <View style={styles.cardInfoContainer}>
        <CustomText text={`${checkup.animal.name}`} />
        <CustomText text={`${checkup.title}`} />

        <View style={extras.cardInfoScheduleContainer}>
          <CustomText
            icon="calendar"
            text={`${dateOnly.toLocaleDateString()}`}
          />
          <CustomText
            icon="clock"
            text={`${timeOnly.hours}:${timeOnly.minutes}`}
          />
        </View>
        <CustomText
          text={`${
            checkup.description
              ? `Description: ${checkup.description}`
              : "No description"
          }`}
        />
      </View>
      <View style={styles.cardButtonsContainer}>
        <CustomButton
          onPress={() =>
            router.navigate({
              pathname: ROUTES.PRIVATE.ANIMAL.DETAILS,
              params: { id: checkup.animal.id },
            })
          }
          text="View Animal"
        />
      </View>
    </Pressable>
  );
}

const extras = StyleSheet.create({
  cardContainer: {
    height: size.height.xxl,
  },
  cardInfoScheduleContainer: {
    flexDirection: "row",
    justifyContent: "space-between",
  },
});
