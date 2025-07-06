import { CheckupPreview } from "@/api/checkup/checkup.output";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import ROUTES from "@/lib/routes";
import { splitDateTime } from "@/lib/utils";
import { useRouter } from "expo-router";
import { Pressable, View } from "react-native";
import SafeImage from "../basic/SafeImage";
import CustomButton from "../basic/custom/CustomButton";
import CustomText from "../basic/custom/CustomText";

export default function CheckupPreviewCard({
  checkup,
}: {
  checkup: CheckupPreview;
}) {
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
      style={styles.cardContainer}
    >
      <SafeImage uri={checkup.animal.imageUrl} />
      <View style={styles.cardInfoContainer}>
        <CustomText text={`${checkup.title}`} />
        <CustomText
          text={`${
            checkup.description
              ? `Description: ${checkup.description}`
              : "No description"
          }`}
        />
        <CustomText
          icon="calendar"
          text={`${dateOnly} ${timeOnly.hours}:${timeOnly.minutes}`}
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
