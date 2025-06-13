import layout from "@/theme/layout";
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
  return (
    <CustomButton
      onPress={onPress}
      icon={icon ? icon : "filter"}
      style={[style, layout.filter]}
    />
  );
}
