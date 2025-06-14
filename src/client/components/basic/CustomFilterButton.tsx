import { useThemedStyles } from "@/hooks/useThemedStyles";
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
      style={[style, styles.filter]}
    />
  );
}
