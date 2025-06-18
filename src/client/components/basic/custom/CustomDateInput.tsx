import { useThemedStyles } from "@/hooks/useThemedStyles";
import { dropMilliseconds } from "@/lib/utils";
import { FontAwesome5 } from "@expo/vector-icons";
import { useState } from "react";
import { Pressable } from "react-native";
import { DatePickerModal } from "react-native-paper-dates";
import CustomText from "./CustomText";

type CustomDateInputProps = {
  value: string;
  onChange: (date: string) => void;
  disabled?: boolean;
};

export default function CustomDateInput({
  value,
  onChange,
  disabled,
}: CustomDateInputProps) {
  const { styles } = useThemedStyles();
  const [visible, setVisible] = useState(false);

  const date = value ? new Date(value) : undefined;

  const onDismiss = () => setVisible(false);

  const onConfirm = ({ date }) => {
    setVisible(false);
    onChange(dropMilliseconds(date.toISOString()));
  };

  return (
    <Pressable
      onPress={() => setVisible(true)}
      disabled={disabled}
      style={styles.button}
    >
      <FontAwesome5 name="calendar" />
      <CustomText
        text={value ? new Date(value).toLocaleDateString() : "Pick a date"}
      />

      <DatePickerModal
        locale="en"
        mode="single"
        visible={visible}
        onDismiss={onDismiss}
        date={date}
        onConfirm={onConfirm}
      />
    </Pressable>
  );
}
