import React, { useState } from "react";
import { View, Button } from "react-native";
import DateTimePicker from "@react-native-community/datetimepicker";

interface MobileDatePickerProps {
  selectedDate: string | null;
  onDateChange: (date: string | null) => void;
}

export default function MobileDatePicker({
  selectedDate,
  onDateChange,
}: MobileDatePickerProps) {
  const [showPicker, setShowPicker] = useState(false);

  const showDatePicker = () => setShowPicker(true);
  const hideDatePicker = () => setShowPicker(false);

  const handleDateChange = (event: any, date: Date | undefined) => {
    if (date) {
      onDateChange(date.toISOString().split("T")[0]);
    }
    hideDatePicker();
  };

  return (
    <View>
      {showPicker && (
        <DateTimePicker
          value={selectedDate ? new Date(selectedDate) : new Date()}
          mode="date"
          onChange={handleDateChange}
        />
      )}
      <Button title="Pick a Date" onPress={showDatePicker} />
      <Button
        title="Clear Date"
        onPress={() => onDateChange(null)}
        disabled={!selectedDate}
      />
    </View>
  );
}
