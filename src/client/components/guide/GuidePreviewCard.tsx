import { useThemedStyles } from "@/hooks/useThemedStyles";
import ROUTES from "@/lib/routes";
import { splitDateTime } from "@/lib/utils";
import { useRouter } from "expo-router";
import { View } from "react-native";
import CustomButton from "../basic/CustomButton";
import CustomText from "../basic/CustomText";
import SafeImage from "../basic/SafeImage";

interface GuidePreviewCardProps {
  guide: GuidePreview;
}

export default function GuidePreviewCard({ guide }: GuidePreviewCardProps) {
  const { colours, styles } = useThemedStyles();
  const router = useRouter();
  const { dateOnly, timeOnly } = splitDateTime(guide.createdAt);

  return (
    <View style={styles.cardContainer}>
      <View style={styles.cardImageContainer}>
        <SafeImage
          uri={guide.imageUrl}
          fallback={require("@/assets/placeholder.png")}
          style={styles.image}
          resizeMode="cover"
        />
      </View>
      <View style={styles.cardInfoContainer}>
        <CustomText text={`${guide.title}`} />
        <CustomText icon="calendar" text={`${dateOnly.toLocaleDateString()}`} />

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
              pathname: ROUTES.PRIVATE.GUIDE.DETAILS,
              params: { id: guide.id },
            })
          }
          text="View Details"
        />
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
    </View>
  );
}
