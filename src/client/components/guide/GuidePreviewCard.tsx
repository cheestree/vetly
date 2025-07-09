import { GuidePreview } from "@/api/guide/guide.output";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import ROUTES from "@/lib/routes";
import { splitDateTime } from "@/lib/utils";
import { useRouter } from "expo-router";
import { Pressable, View } from "react-native";
import SafeImage from "../basic/SafeImage";
import CustomButton from "../basic/custom/CustomButton";
import CustomText from "../basic/custom/CustomText";

type GuidePreviewCardProps = {
  guide: GuidePreview;
};

export default function GuidePreviewCard({ guide }: GuidePreviewCardProps) {
  const { styles } = useThemedStyles();
  const router = useRouter();
  const { dateOnly } = splitDateTime(guide.createdAt);

  return (
    <Pressable
      onPress={() =>
        router.navigate({
          pathname: ROUTES.PRIVATE.GUIDE.DETAILS,
          params: { id: guide.id },
        })
      }
      style={styles.cardContainer}
    >
      <SafeImage uri={guide.imageUrl} />
      <View style={styles.cardInfoContainer}>
        <CustomText text={`${guide.title}`} />
        <CustomText icon="calendar" text={`${dateOnly}`} />

        <CustomText
          text={
            guide.description
              ? `Description: ${guide.description}`
              : "No description"
          }
        />
      </View>
      <View style={styles.cardButtonsContainer}>
        <CustomButton
          onPress={() =>
            router.navigate({
              pathname: ROUTES.PRIVATE.USER.DETAILS,
              params: { id: guide.author.id },
            })
          }
          text="View Author"
        />
      </View>
    </Pressable>
  );
}
