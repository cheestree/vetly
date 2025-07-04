import { AnimalQueryParams } from "@/api/animal/animal.input";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { format, formatDate } from "date-fns";
import { useCallback, useState } from "react";
import { View } from "react-native";
import { Modal } from "react-native-paper";
import { DatePickerModal } from "react-native-paper-dates";
import { CalendarDate } from "react-native-paper-dates/lib/typescript/Date/Calendar";
import ModalFooter from "../basic/base/ModalFooter";
import CustomButton from "../basic/custom/CustomButton";
import LabeledSwitch from "../basic/custom/CustomLabeledSwitch";
import CustomText from "../basic/custom/CustomText";
import CustomTextInput from "../basic/custom/CustomTextInput";

type AnimalFilterModalProps = {
  visible: boolean;
  onDismiss: () => void;
  onSearch: (params: Partial<AnimalQueryParams>) => void;
  canSearchByUserEmail: boolean;
};

export default function AnimalFilterModal({
  visible,
  onDismiss,
  onSearch,
  canSearchByUserEmail,
}: AnimalFilterModalProps) {
  const { styles } = useThemedStyles();
  const [open, setOpen] = useState(false);

  const [filters, setFilters] = useState({
    birthDate: undefined as Date | undefined,
    userEmail: "",
    name: "",
    microchip: "",
    species: "",
    mine: null as boolean | null,
    active: true as boolean | null,
  });

  const onDismissDate = useCallback(() => {
    setOpen(false);
  }, []);

  const onConfirmDate = useCallback((date: { date: CalendarDate }) => {
    setOpen(false);
    setFilters((prev) => ({ ...prev, birthDate: date.date }));
  }, []);

  const handleSearch = () => {
    const params: Partial<AnimalQueryParams> = {
      birthDate: filters.birthDate
        ? formatDate(filters.birthDate, "yyyy-MM-dd")
        : undefined,
      userEmail:
        canSearchByUserEmail && filters.userEmail.trim() !== ""
          ? filters.userEmail
          : undefined,
      self: filters.mine,
      active: filters.active,
      name: filters.name.trim() !== "" ? filters.name : undefined,
      microchip:
        filters.microchip.trim() !== "" ? filters.microchip : undefined,
      species: filters.species.trim() !== "" ? filters.species : undefined,
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
          <CustomButton onPress={() => setOpen(true)} text="Pick birth date" />
          <DatePickerModal
            locale="en"
            mode="single"
            visible={open}
            onDismiss={onDismissDate}
            date={filters.birthDate}
            onConfirm={onConfirmDate}
          />
          {filters.birthDate && (
            <CustomText text={format(filters.birthDate, "MMM d, yyyy")} />
          )}
        </View>
        <CustomTextInput
          placeholder="Name"
          value={filters.name}
          onChangeText={(text) =>
            setFilters((prev) => ({ ...prev, name: text }))
          }
        />
        <CustomTextInput
          placeholder="Microchip"
          value={filters.microchip}
          onChangeText={(text) =>
            setFilters((prev) => ({ ...prev, microchip: text }))
          }
        />
        <CustomTextInput
          placeholder="Species"
          value={filters.species}
          onChangeText={(text) =>
            setFilters((prev) => ({ ...prev, species: text }))
          }
        />
        {canSearchByUserEmail && (
          <CustomTextInput
            placeholder="User Email"
            value={filters.userEmail}
            onChangeText={(text) =>
              setFilters((prev) => ({ ...prev, userEmail: text }))
            }
            keyboardType="numeric"
          />
        )}
        {canSearchByUserEmail && (
          <>
            <LabeledSwitch
              label="Mine?"
              value={filters.mine}
              onValueChange={(value) =>
                setFilters((prev) => ({ ...prev, mine: value }))
              }
              tristate
            />
            <LabeledSwitch
              label="Active?"
              value={filters.active}
              onValueChange={(value) =>
                setFilters((prev) => ({ ...prev, active: value }))
              }
              tristate
            />
          </>
        )}
        <ModalFooter handleSearch={handleSearch} onDismiss={onDismiss} />
      </View>
    </Modal>
  );
}
