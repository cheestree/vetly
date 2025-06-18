import { TextStyle, ViewStyle } from "react-native";
import CustomButton from "./CustomButton";

type ToggleItemsProps = {
  label: string;
  icon: string;
  value: any;
};

type CustomToggleableButtonProps = {
  list: ToggleItemsProps[];
  value: any;
  onChange: (newValue: any) => void;
  stylePressable?: ViewStyle;
  styleText?: TextStyle;
};

export default function CustomToggleableButton({
  list,
  value,
  onChange,
  stylePressable,
  styleText,
}: CustomToggleableButtonProps) {
  const currentIndex = list.findIndex((item) => item.value === value);

  const handlePress = () => {
    const nextIndex = (currentIndex + 1) % list.length;
    onChange(list[nextIndex].value);
  };

  const currentItem = list[currentIndex] ?? list[0];

  return (
    <CustomButton
      stylePressable={stylePressable}
      styleText={styleText}
      icon={currentItem.icon}
      onPress={handlePress}
      text={currentItem.label}
    />
  );
}
