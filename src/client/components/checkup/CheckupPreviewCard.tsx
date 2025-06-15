import { useThemedStyles } from "@/hooks/useThemedStyles";
import ROUTES from "@/lib/routes";
import { splitDateTime } from "@/lib/utils";
import { useRouter } from "expo-router";
import { Pressable, View } from "react-native";
import CustomButton from "../basic/CustomButton";
import CustomText from "../basic/CustomText";
import SafeImage from "../basic/SafeImage";

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
      style={[styles.cardContainer]}
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

        <CustomText icon="calendar" text={`${dateOnly.toLocaleDateString()}`} />
        <CustomText
          icon="clock"
          text={`${timeOnly.hours}:${timeOnly.minutes}`}
        />
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
