import { SupplyPreview } from "@/api/supply/supply.output";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import ROUTES from "@/lib/routes";
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
        router.navigate({
          pathname: ROUTES.PRIVATE.INVENTORY.DETAILS,
          params: { id: supply.id },
        })
      }
      style={styles.cardContainer}
    >
      <CustomText text={supply.name} />
      <CustomText text={`Type: ${supply.type}`} />
    </Pressable>
  );
}
