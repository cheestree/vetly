import { useThemedStyles } from "@/hooks/useThemedStyles";
import { View } from "react-native";
import { Modal } from "react-native-paper";
import CustomButton from "../basic/custom/CustomButton";

interface GuideFilterModalProps {
  visible: boolean;
  onDismiss: () => void;
  onSearch: (params: GuideQueryParams) => void;
}

export default function GuideFilterModal({
  visible,
  onDismiss,
  onSearch,
}: GuideFilterModalProps) {
  const { styles } = useThemedStyles();

  const handleSearch = () => {
    const params: Partial<GuideQueryParams> = {};

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
