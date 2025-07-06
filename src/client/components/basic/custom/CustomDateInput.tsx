import { useThemedStyles } from "@/hooks/useThemedStyles";
import { FontAwesome5 } from "@expo/vector-icons";
import React, { useState } from "react";
import { Pressable, View } from "react-native";
import { DatePickerModal, TimePickerModal } from "react-native-paper-dates";
import CustomText from "./CustomText";

type CustomDateInputProps = {
  value: string | undefined;
  onChange: (date: string) => void;
  disabled?: boolean;
  mode?: "date" | "dateTime";
};

export default function CustomDateInput({
  value,
  onChange,
  disabled,
  mode = "date",
}: CustomDateInputProps) {
  const { styles } = useThemedStyles();
  const [dateVisible, setDateVisible] = useState(false);
  const [timeVisible, setTimeVisible] = useState(false);

  const date = value ? new Date(value) : new Date();
  const [selectedDate, setSelectedDate] = useState<Date | undefined>();
  const [selectedTime, setSelectedTime] = useState<
    { hours: number; minutes: number } | undefined
  >();

  const showDatePicker = () => setDateVisible(true);
  const dismissDatePicker = () => setDateVisible(false);
  const dismissTimePicker = () => setTimeVisible(false);

  const onDateConfirm = (params: { date: Date | undefined }) => {
    dismissDatePicker();
    if (params.date) {
      setSelectedDate(params.date);
      if (mode === "dateTime") {
        setTimeVisible(true);
      } else {
        const dateOnly = [
          params.date.getFullYear(),
          (params.date.getMonth() + 1).toString().padStart(2, "0"),
          params.date.getDate().toString().padStart(2, "0"),
        ].join("-");
        onChange(dateOnly);
      }
    }
  };

  const onTimeConfirm = ({
    hours,
    minutes,
  }: {
    hours: number;
    minutes: number;
  }) => {
    dismissTimePicker();
    if (selectedDate) {
      const updatedDate = new Date(selectedDate);
      updatedDate.setHours(hours, minutes, 0, 0);
      const isoString = updatedDate.toISOString();
      onChange(isoString);
    }
  };

  return (
    <View style={{ flex: 1 }}>
      <Pressable
        onPress={showDatePicker}
        disabled={disabled}
        style={[
          styles.button,
          {
            flexDirection: "row",
            alignItems: "center",
            justifyContent: "flex-start",
            paddingVertical: 12,
          },
        ]}
      >
        <FontAwesome5 name="calendar" />
        <CustomText
          text={
            value
              ? new Date(value).toLocaleString()
              : mode === "dateTime"
                ? "Pick date & time"
                : "Pick a date"
          }
          style={{ marginLeft: 8 }}
        />
      </Pressable>

      <DatePickerModal
        locale="en"
        mode="single"
        visible={dateVisible}
        onDismiss={dismissDatePicker}
        date={date}
        onConfirm={onDateConfirm}
      />

      {mode === "dateTime" && (
        <TimePickerModal
          visible={timeVisible}
          onDismiss={dismissTimePicker}
          onConfirm={onTimeConfirm}
          hours={selectedDate?.getHours() ?? 12}
          minutes={selectedDate?.getMinutes() ?? 0}
        />
      )}
    </View>
  );
}
