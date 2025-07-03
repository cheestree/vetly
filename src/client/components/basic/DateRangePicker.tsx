import size from "@/theme/size";
import { useState } from "react";
import { View } from "react-native";
import { DatePickerModal } from "react-native-paper-dates";
import CustomButton from "../basic/custom/CustomButton";
import DateRangeDisplay from "../basic/DateDisplay";

type DateRangePickerProps = {
  startDate?: Date;
  endDate?: Date;
  onChange: (range: { startDate?: Date; endDate?: Date }) => void;
};

export default function DateRangePicker({
  startDate,
  endDate,
  onChange,
}: DateRangePickerProps) {
  const [open, setOpen] = useState(false);

  const handleConfirm = ({
    startDate,
    endDate,
  }: {
    startDate?: Date;
    endDate?: Date;
  }) => {
    setOpen(false);
    onChange({ startDate, endDate });
  };

  const handleReset = () => {
    onChange({ startDate: undefined, endDate: undefined });
  };

  return (
    <View style={{ alignItems: "center", gap: size.gap.sm }}>
      <View
        style={{ flexDirection: "row", alignItems: "center", gap: size.gap.sm }}
      >
        <CustomButton
          onPress={() => setOpen(true)}
          text="Pick submitted date range"
        />
        {(startDate || endDate) && (
          <CustomButton
            icon="trash"
            text=""
            onPress={handleReset}
            variant="danger"
          />
        )}
      </View>
      <DatePickerModal
        locale="en"
        mode="range"
        visible={open}
        onDismiss={() => setOpen(false)}
        startDate={startDate}
        endDate={endDate}
        onConfirm={handleConfirm}
      />
      <DateRangeDisplay start={startDate} end={endDate} />
    </View>
  );
}
