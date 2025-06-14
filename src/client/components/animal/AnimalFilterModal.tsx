import { useThemedStyles } from "@/hooks/useThemedStyles";
import { RangeProps } from "@/lib/types";
import spacing from "@/theme/spacing";
import { format } from "date-fns";
import { useCallback, useState } from "react";
import { Pressable, StyleSheet, View } from "react-native";
import { Modal, Text, TextInput } from "react-native-paper";
import { DatePickerModal } from "react-native-paper-dates";
import CustomButton from "../basic/CustomButton";
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
  const { styles } = useThemedStyles();
  const [open, setOpen] = useState(false);

  const [range, setRange] = useState<{ startDate?: Date; endDate?: Date }>({});
  const [userId, setUserId] = useState("");
  const [mine, setMine] = useState<boolean | null>(null);
  const [active, setActive] = useState<boolean | null>(null);

  const onDismissRange = useCallback(() => {
    setOpen(false);
  }, [setOpen]);

  const onConfirmRange = useCallback(
    ({ startDate, endDate }: RangeProps) => {
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
      active: active,
    };

    onSearch(params);
  };

  return (
    <Modal
      visible={visible}
      onDismiss={onDismiss}
      contentContainerStyle={styles.modalContainer}
    >
      <View style={styles.modalContainer}>
        <View style={extras.modalFilters}>
          <View>
            <Pressable onPress={() => setOpen(true)} style={styles.button}>
              <Text style={styles.buttonText}>Pick date range</Text>
            </Pressable>
            <DatePickerModal
              mode="range"
              locale={"en-EN"}
              visible={open}
              onDismiss={onDismissRange}
              startDate={range.startDate}
              endDate={range.endDate}
              onConfirm={onConfirmRange}
            />
            {range.startDate && range.endDate && (
              <Text style={extras.rangeText}>
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
          <LabeledSwitch
            label="Active?"
            value={active}
            onValueChange={setActive}
            tristate
            disabled={!canSearchByUserId}
          />
        </View>

        <View style={extras.modalButtons}>
          <CustomButton onPress={handleSearch} text="Search" />
          <CustomButton onPress={onDismiss} text="Close" />
        </View>
      </View>
    </Modal>
  );
}

const extras = StyleSheet.create({
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
