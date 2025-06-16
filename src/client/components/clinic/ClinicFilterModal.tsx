import { useThemedStyles } from "@/hooks/useThemedStyles";
import { View } from "react-native";
import { Modal } from "react-native-paper";
import CustomButton from "../basic/custom/CustomButton";

interface ClinicFilterModalProps {
  visible: boolean;
  onDismiss: () => void;
  onSearch: (params: Partial<ClinicQueryParams>) => void;
}

export default function ClinicFilterModal({
  visible,
  onDismiss,
  onSearch,
}: ClinicFilterModalProps) {
  const { styles } = useThemedStyles();

  const handleSearch = () => {
    const params: Partial<ClinicQueryParams> = {};

    onSearch(params);
  };

  return (
    <Modal
      visible={visible}
      onDismiss={onDismiss}
      contentContainerStyle={styles.modalContainer}
    >
      <View style={styles.modalContainer}>
        <View style={styles.modalFilters}></View>

        <View style={styles.modalButtons}>
          <CustomButton onPress={handleSearch} text="Search" />
          <CustomButton onPress={onDismiss} text="Close" />
        </View>
      </View>
    </Modal>
  );
}
