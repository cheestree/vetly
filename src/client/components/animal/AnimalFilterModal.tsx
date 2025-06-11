
import { StyleSheet, View } from "react-native";
import { Modal, Button, Text, TextInput } from "react-native-paper";
import { DatePickerModal } from "react-native-paper-dates";
import LabeledSwitch from "../basic/LabeledSwitch";
import colours from "@/theme/colours";
import size from "@/theme/size";
import spacing from "@/theme/spacing";
import { format } from "date-fns";
import { useState } from "react";

interface AnimalFilterModalProps {
  visible: boolean;
  onDismiss: () => void;
  onSearch: (params: Partial<AnimalQueryParams>) => void;
  canSearchByUserId: boolean;
}

export default function AnimalFilterModal({
  visible,
  onDismiss,
  onSearch,
  canSearchByUserId,
}: AnimalFilterModalProps) {
  const [open, setOpen] = useState(false);

  const [range, setRange] = useState<{ startDate?: Date; endDate?: Date }>({});
  const [userId, setUserId] = useState("");
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
      userId: canSearchByUserId && userId.trim() !== "" ? userId : undefined,
      self: mine,
    };

    onSearch(params);
  };

  return (
    <Modal
      visible={visible}
      onDismiss={onDismiss}
      contentContainerStyle={styles.modalContainer}
    >
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

        {canSearchByUserId && (
          <TextInput
            placeholder="User ID"
            value={userId}
            onChangeText={setUserId}
            keyboardType="numeric"
          />
        )}

        <LabeledSwitch
          label="Mine?"
          value={mine}
          onValueChange={setMine}
          tristate
          disabled={mineDisabled || !canSearchByUserId}
        />
      </View>

      <View style={styles.modalButtons}>
        <Button style={styles.modalButton} onPress={handleSearch}>
          Search
        </Button>
        <Button style={styles.modalButton} onPress={onDismiss}>
          Close
        </Button>
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
    color: "gray",
  },
});
