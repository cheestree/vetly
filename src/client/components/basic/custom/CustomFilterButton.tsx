import { useThemedStyles } from "@/hooks/useThemedStyles";
import { StyleProp, ViewStyle } from "react-native";
import CustomButton from "./CustomButton";

type CustomFilterButtonProps = {
  icon?: React.ReactNode;
  onPress: () => void;
  style?: StyleProp<ViewStyle>;
};

export default function CustomFilterButton({
  icon,
  onPress,
  style,
}: CustomFilterButtonProps) {
  const { styles } = useThemedStyles();

  return (
    <CustomButton
      onPress={onPress}
      icon={icon ? icon : "filter"}
      stylePressable={styles.filter}
    />
  );
}
