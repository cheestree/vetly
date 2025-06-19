import { useThemedStyles } from "@/hooks/useThemedStyles";
import { View } from "react-native";
import CustomImage from "../basic/custom/CustomImage";
import CustomText from "../basic/custom/CustomText";

export default function SupplyDetailsContent(supply: SupplyInformation) {
  const { styles } = useThemedStyles();

  return (
    <View style={styles.container}>
      <CustomText text={supply.name} />
      <CustomText text={supply.description || "No description available."} />
      <CustomImage url={supply.imageUrl} />
    </View>
  );
}
