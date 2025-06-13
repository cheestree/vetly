import { getTranslations } from "@/handlers/Handlers";
import layout from "@/theme/layout";
import spacing from "@/theme/spacing";
import { format } from "date-fns";
import { useCallback, useState } from "react";
import { Pressable, StyleSheet, View } from "react-native";
import { Modal, Text, TextInput } from "react-native-paper";
import { DatePickerModal } from "react-native-paper-dates";
import LabeledSwitch from "../basic/LabeledSwitch";

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

  const onDismissRange = useCallback(() => {
    setOpen(false);
  }, [setOpen]);

  const onConfirmRange = useCallback(
    ({ startDate, endDate }) => {
      setOpen(false);
      setRange({ startDate, endDate });
    },
    [setOpen, setRange],
  );

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
      contentContainerStyle={layout.modalContainer}
    >
      <View style={layout.modalContainer}>
        <View style={styles.modalFilters}>
          <View>
            <Pressable onPress={() => setOpen(true)} style={layout.button}>
              <Text style={layout.buttonText}>Pick date range</Text>
            </Pressable>
            <DatePickerModal
              mode="range"
              locale={getTranslations()}
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
            disabled={!canSearchByUserId}
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
