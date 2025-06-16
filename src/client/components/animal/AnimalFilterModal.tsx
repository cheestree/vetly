import { useThemedStyles } from "@/hooks/useThemedStyles";
import { RangeProps } from "@/lib/types";
import { format } from "date-fns";
import { useCallback, useState } from "react";
import { View } from "react-native";
import { Modal } from "react-native-paper";
import { DatePickerModal } from "react-native-paper-dates";
import CustomButton from "../basic/custom/CustomButton";
import LabeledSwitch from "../basic/custom/CustomLabeledSwitch";
import CustomText from "../basic/custom/CustomText";
import CustomTextInput from "../basic/custom/CustomTextInput";

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
  const [range, setRange] = useState<RangeProps>({
    startDate: undefined,
    endDate: undefined,
  });

  const [userId, setUserId] = useState("");
  const [mine, setMine] = useState<boolean | null>(null);
  const [active, setActive] = useState<boolean | null>(true);

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
        <View style={styles.modalFilters}>
          <View>
            <CustomButton
              onPress={() => setOpen(true)}
              text="Pick date range"
            />
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
              <CustomText
                text={`${format(range.startDate, "MMM d, yyyy")} - ${format(range.endDate, "MMM d, yyyy")}`}
              />
            )}
          </View>

          {canSearchByUserId && (
            <CustomTextInput
              placeholder="User ID"
              value={userId}
              onChangeText={setUserId}
              keyboardType="numeric"
            />
          )}

          {canSearchByUserId && (
            <>
              <LabeledSwitch
                label="Mine?"
                value={mine}
                onValueChange={setMine}
                tristate
              />
              <LabeledSwitch
                label="Active?"
                value={active}
                onValueChange={setActive}
                tristate
              />
            </>
          )}

          <View style={styles.modalButtons}>
            <CustomButton onPress={handleSearch} text="Search" />
            <CustomButton onPress={onDismiss} text="Close" />
          </View>
        </View>
      </View>
    </Modal>
  );
}
