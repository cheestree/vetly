import spacing from "@/theme/spacing";
import { format } from "date-fns";
import { useState } from "react";
import { StyleSheet, View } from "react-native";
import { Modal, Button, Text } from "react-native-paper";
import { DatePickerModal } from "react-native-paper-dates";
import LabeledSwitch from "../basic/LabeledSwitch";
import colours from "@/theme/colours";
import size from "@/theme/size";
import layout from "@/theme/layout";

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
  const [mine, setMine] = useState(false);
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
    <Modal visible={visible} onDismiss={onDismiss}>
      <View style={styles.modalContainer}>
        <View style={styles.modalFilters}>
          <View>
            <Button onPress={() => setOpen(true)} style={styles.modalButton}>
              <Text style={layout.baseButton}>Pick date range</Text>
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
          <Button style={styles.modalButton} onPress={handleSearch}>
            <Text style={layout.baseButton}>Search</Text>
          </Button>
          <Button style={styles.modalButton} onPress={onDismiss}>
            <Text style={layout.baseButton}>Close</Text>
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
    backgroundColor: colours.primary,
    borderRadius: size.border.sm,
    color: colours.fontPrimary,
  },
  rangeText: {
    marginLeft: spacing.sm,
    fontSize: 14,
  },
});
