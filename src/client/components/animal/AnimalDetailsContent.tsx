import { AnimalInformation } from "@/api/animal/animal.output";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { View } from "react-native";
import CustomImage from "../basic/custom/CustomImage";
import CustomText from "../basic/custom/CustomText";

export default function AnimalDetailsContent({
  animal,
}: {
  animal?: AnimalInformation;
}) {
  const { styles } = useThemedStyles();
  if (!animal) {
    return <View style={styles.container}></View>;
  }

  return (
    <View style={styles.container}>
      <CustomText text={animal.name} />
      <CustomText text={animal.species || "Unknown"} />

      <CustomImage url={animal.imageUrl} />
    </View>
  );
}
