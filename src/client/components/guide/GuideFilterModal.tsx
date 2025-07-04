import { GuideQueryParams } from "@/api/guide/guide.input";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { useState } from "react";
import { View } from "react-native";
import { Modal } from "react-native-paper";
import ModalFooter from "../basic/base/ModalFooter";
import CustomTextInput from "../basic/custom/CustomTextInput";

type GuideFilterModalProps = {
  visible: boolean;
  onDismiss: () => void;
  onSearch: (params: Partial<GuideQueryParams>) => void;
};

export default function GuideFilterModal({
  visible,
  onDismiss,
  onSearch,
}: GuideFilterModalProps) {
  const { styles } = useThemedStyles();

  const [filters, setFilters] = useState({
    title: "",
  });

  const handleSearch = () => {
    const params: Partial<GuideQueryParams> = {
      title: filters.title.trim() !== "" ? filters.title : undefined,
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
        <CustomTextInput
          placeholder="Guide Title"
          value={filters.title}
          onChangeText={(text) =>
            setFilters((prev) => ({ ...prev, title: text }))
          }
        />
        <ModalFooter handleSearch={handleSearch} onDismiss={onDismiss} />
      </View>
    </Modal>
  );
}
