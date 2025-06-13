import layout from "@/theme/layout";
import spacing from "@/theme/spacing";
import { format } from "date-fns";
import { useState } from "react";
import { Pressable, StyleSheet, View } from "react-native";
import { Modal, Text } from "react-native-paper";
import { DatePickerModal } from "react-native-paper-dates";
import LabeledSwitch from "../basic/LabeledSwitch";

interface CheckupFilterModalProps {
  visible: boolean;
  onDismiss: () => void;
  onSearch: (params: CheckupQueryParams) => void;
}

export default function CheckupFilterModal({
  visible,
  onDismiss,
  onSearch,
}: CheckupFilterModalProps) {
  const [open, setOpen] = useState(false);

  const [range, setRange] = useState<{ startDate?: Date; endDate?: Date }>({});
  const [mine, setMine] = useState<boolean | null>(false);
  const [mineDisabled, setMineDisabled] = useState(false);

  const onDismissRange = () => setOpen(false);

  const onConfirmRange = ({
    startDate,
    endDate,
  }: {
    startDate?: Date;
    endDate?: Date;
  }) => {
    setOpen(false);
    setRange({ startDate, endDate });
  };

  const handleSearch = () => {
    const params: Partial<AnimalQueryParams> = {
      birthDate: range.startDate?.getTime(),
      self: mine,
    };

    onSearch(params);
  };
  return (
    <Modal
      visible={visible}
      onDismiss={onDismiss}
      contentContainerStyle={layout.modalContainer}
    >
      <View style={layout.modalContainer}>
        <View style={styles.modalFilters}>
          <View>
            <Pressable onPress={() => setOpen(true)} style={layout.button}>
              <Text style={layout.buttonText}>Pick date range</Text>
            </Pressable>
            <DatePickerModal
              locale="en"
              mode="range"
              visible={open}
              onDismiss={onDismissRange}
              startDate={range.startDate}
              endDate={range.endDate}
              onConfirm={onConfirmRange}
            />
            {range.startDate && range.endDate && (
              <Text style={styles.rangeText}>
                {format(range.startDate, "MMM d, yyyy")} -{" "}
                {format(range.endDate, "MMM d, yyyy")}
              </Text>
            )}
          </View>

          <LabeledSwitch
            label="Mine?"
            value={mine}
            onValueChange={setMine}
            tristate
            disabled={mineDisabled}
          />
        </View>

        <View style={styles.modalButtons}>
          <Pressable style={layout.button} onPress={handleSearch}>
            <Text style={layout.buttonText}>Search</Text>
          </Pressable>
          <Pressable style={layout.button} onPress={onDismiss}>
            <Text style={layout.buttonText}>Close</Text>
          </Pressable>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  modalFilters: {
    width: "100%",
    gap: spacing.md,
    marginBottom: spacing.md,
  },
  modalButtons: {
    flexDirection: "row",
    justifyContent: "space-around",
    width: "100%",
  },
  rangeText: {
    marginLeft: spacing.sm,
    fontSize: 14,
    color: "gray",
  },
});
