import { CheckupQueryParams } from "@/api/checkup/checkup.input";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { format } from "date-fns";
import { useState } from "react";
import { View } from "react-native";
import { Modal } from "react-native-paper";
import { DatePickerModal } from "react-native-paper-dates";
import ModalFooter from "../basic/base/ModalFooter";
import CustomButton from "../basic/custom/CustomButton";
import CustomText from "../basic/custom/CustomText";
import CustomTextInput from "../basic/custom/CustomTextInput";

type CheckupFilterModalProps = {
  visible: boolean;
  onDismiss: () => void;
  onSearch: (params: CheckupQueryParams) => void;
  canSearchByUserId: boolean;
};

export default function CheckupFilterModal({
  visible,
  onDismiss,
  onSearch,
  canSearchByUserId,
}: CheckupFilterModalProps) {
  const { styles } = useThemedStyles();
  const [open, setOpen] = useState(false);

  const [filters, setFilters] = useState({
    veterinarianId: "",
    veterinarianName: "",
    animalId: "",
    animalName: "",
    clinicId: "",
    clinicName: "",
    startDate: undefined as Date | undefined,
    endDate: undefined as Date | undefined,
  });

  const onDismissRange = () => setOpen(false);

  const onConfirmRange = ({
    startDate,
    endDate,
  }: {
    startDate?: Date;
    endDate?: Date;
  }) => {
    setOpen(false);
    setFilters((prev) => ({ ...prev, startDate, endDate }));
  };

  const handleSearch = () => {
    const params: Partial<CheckupQueryParams> = {
      veterinarianId:
        filters.veterinarianId.trim() !== ""
          ? filters.veterinarianId
          : undefined,
      veterinarianName:
        filters.veterinarianName.trim() !== ""
          ? filters.veterinarianName
          : undefined,
      animalId:
        filters.animalId.trim() !== "" && !isNaN(Number(filters.animalId))
          ? Number(filters.animalId)
          : undefined,
      animalName:
        filters.animalName.trim() !== "" ? filters.animalName : undefined,
      clinicId:
        filters.clinicId.trim() !== "" && !isNaN(Number(filters.clinicId))
          ? Number(filters.clinicId)
          : undefined,
      clinicName:
        filters.clinicName.trim() !== "" ? filters.clinicName : undefined,
      dateTimeStart: filters.startDate
        ? format(filters.startDate, "yyyy-MM-dd")
        : undefined,
      dateTimeEnd: filters.endDate
        ? format(filters.endDate, "yyyy-MM-dd")
        : undefined,
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
          <CustomButton onPress={() => setOpen(true)} text="Pick date range" />
          <DatePickerModal
            locale="en"
            mode="range"
            visible={open}
            onDismiss={onDismissRange}
            startDate={filters.startDate}
            endDate={filters.endDate}
            onConfirm={onConfirmRange}
          />
          {filters.startDate && filters.endDate && (
            <CustomText
              text={`${format(filters.startDate, "MMM d, yyyy")} - ${format(filters.endDate, "MMM d, yyyy")}`}
            />
          )}
        </View>
        {canSearchByUserId && (
          <CustomTextInput
            placeholder="Veterinarian ID"
            value={filters.veterinarianId}
            onChangeText={(text) =>
              setFilters((prev) => ({ ...prev, veterinarianId: text }))
            }
          />
        )}
        <CustomTextInput
          placeholder="Veterinarian Name"
          value={filters.veterinarianName}
          onChangeText={(text) =>
            setFilters((prev) => ({ ...prev, veterinarianName: text }))
          }
        />
        {canSearchByUserId && (
          <CustomTextInput
            placeholder="Animal ID"
            value={filters.animalId}
            onChangeText={(text) =>
              setFilters((prev) => ({ ...prev, animalId: text }))
            }
            keyboardType="numeric"
          />
        )}
        <CustomTextInput
          placeholder="Animal Name"
          value={filters.animalName}
          onChangeText={(text) =>
            setFilters((prev) => ({ ...prev, animalName: text }))
          }
        />
        {canSearchByUserId && (
          <CustomTextInput
            placeholder="Clinic ID"
            value={filters.clinicId}
            onChangeText={(text) =>
              setFilters((prev) => ({ ...prev, clinicId: text }))
            }
            keyboardType="numeric"
          />
        )}
        <CustomTextInput
          placeholder="Clinic Name"
          value={filters.clinicName}
          onChangeText={(text) =>
            setFilters((prev) => ({ ...prev, clinicName: text }))
          }
        />
        <ModalFooter handleSearch={handleSearch} onDismiss={onDismiss} />
      </View>
    </Modal>
  );
}
