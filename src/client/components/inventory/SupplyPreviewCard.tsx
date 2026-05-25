import { SupplyPreview } from "@/api/supply/supply.output";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import ROUTES, { routeWithId } from "@/lib/routes";
import { router } from "expo-router";
import { Pressable } from "react-native";
import CustomText from "../basic/custom/CustomText";

type SupplyPreviewCardProps = {
  supply: SupplyPreview;
};

export default function SupplyPreviewCard({ supply }: SupplyPreviewCardProps) {
  const { styles } = useThemedStyles();

  return (
    <Pressable
      onPress={() =>
        router.navigate(
          routeWithId(ROUTES.PRIVATE.INVENTORY.DETAILS, supply.id),
        )
      }
      style={styles.cardContainer}
    >
      <CustomText text={supply.name} />
      <CustomText text={`Type: ${supply.type}`} />
    </Pressable>
  );
}
