import { useThemedStyles } from "@/hooks/useThemedStyles";
import { format } from "date-fns";
import { useState } from "react";
import { View } from "react-native";
import { Modal } from "react-native-paper";
import { DatePickerModal } from "react-native-paper-dates";
import CustomButton from "../basic/custom/CustomButton";
import LabeledSwitch from "../basic/custom/CustomLabeledSwitch";
import CustomText from "../basic/custom/CustomText";

interface CheckupFilterModalProps {
  visible: boolean;
  onDismiss: () => void;
  onSearch: (params: CheckupQueryParams) => void;
  canSearchByUserId: boolean;
}

export default function CheckupFilterModal({
  visible,
  onDismiss,
  onSearch,
  canSearchByUserId,
}: CheckupFilterModalProps) {
  const { styles } = useThemedStyles();
  const [open, setOpen] = useState(false);

  const [range, setRange] = useState<{ startDate?: Date; endDate?: Date }>({});
  const [mine, setMine] = useState<boolean | null>(false);

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
            <LabeledSwitch
              label="Mine?"
              value={mine}
              onValueChange={setMine}
              tristate
            />
          )}
        </View>

        <View style={styles.modalButtons}>
          <CustomButton onPress={handleSearch} text="Search" />
          <CustomButton onPress={onDismiss} text="Close" />
        </View>
      </View>
    </Modal>
  );
}
