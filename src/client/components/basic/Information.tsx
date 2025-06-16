import { useThemedStyles } from "@/hooks/useThemedStyles";
import { View } from "react-native";
import { ActivityIndicator } from "react-native-paper";
import CustomText from "./custom/CustomText";

type InformationContainerProps<T> = {
  header: string;
  headerModifier?: string;
  headerModifierCondition?: boolean;
  value: string;
  description: string;
  loading: boolean;
};

export default function Information<T>({
  header,
  headerModifier,
  headerModifierCondition,
  value,
  description,
  loading,
}: InformationContainerProps<T>) {
  const { colours, styles } = useThemedStyles();

  return (
    <View>
      {loading ? (
        <ActivityIndicator />
      ) : (
        <>
          <CustomText text={header} />
          <CustomText text={value} />
          <CustomText text={description} />
        </>
      )}
    </View>
  );
}
