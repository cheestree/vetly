import spacing from "@/theme/spacing";
import { format } from "date-fns";
import { useState } from "react";
import { StyleSheet, View } from "react-native";
import { Modal, Button, Text } from "react-native-paper";
import { DatePickerModal } from "react-native-paper-dates";
import LabeledSwitch from "../basic/LabeledSwitch";

interface FilterModalProps {
  visible: boolean;
  onDismiss: () => void;
  onSearch: () => void;
  range: {
    startDate?: Date;
    endDate?: Date;
  };
  setRange: (range: { startDate?: Date; endDate?: Date }) => void;
  mine: boolean;
  setMine: (value: boolean) => void;
  mineDisabled: boolean;
  setMineDisabled: (value: boolean) => void;
}

interface RangeProps {
  startDate?: Date;
  endDate?: Date;
}

export default function FilterModal({
  visible,
  onDismiss,
  onSearch,
  range,
  setRange,
  mine,
  setMine,
  mineDisabled,
  setMineDisabled,
}: FilterModalProps) {
  const [open, setOpen] = useState(false);

  const onDismissRange = () => setOpen(false);

  const onConfirmRange = ({ startDate, endDate }: RangeProps) => {
    setOpen(false);
    setRange({ startDate, endDate });
  };

  return (
    <Modal visible={visible} onDismiss={onDismiss}>
      <View style={styles.modalContainer}>
        <View style={styles.modalFilters}>
          <View>
            <Button onPress={() => setOpen(true)} style={styles.modalButton}>
              Pick date range
            </Button>
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
            label={"Disable 'Mine'"}
            value={mineDisabled}
            onValueChange={(value) => {
              setMineDisabled(value);
              setMine(false);
            }}
          />
          <LabeledSwitch
            label={"Mine?"}
            value={mine}
            onValueChange={setMine}
            disabled={mineDisabled}
          />
        </View>

        <View style={styles.modalButtons}>
          <Button style={styles.modalButton} onPress={onSearch}>
            Search
          </Button>
          <Button style={styles.modalButton} onPress={onDismiss}>
            Close
          </Button>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  modalContainer: {
    flex: 1,
    backgroundColor: "white",
    padding: spacing.md,
    justifyContent: "center",
    alignItems: "center",
  },
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
  modalButton: {
    flex: 1,
    marginHorizontal: spacing.sm,
    borderColor: "#6200ee",
    backgroundColor: "#6200ee",
  },
  rangeText: {
    marginLeft: spacing.sm,
    fontSize: 14,
    color: "gray",
  },
});
