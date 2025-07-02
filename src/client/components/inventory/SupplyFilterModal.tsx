import { SupplyQueryParams, SupplyType } from "@/api/supply/supply.input";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { useState } from "react";
import { View } from "react-native";
import { Modal } from "react-native-paper";
import ModalFooter from "../basic/base/ModalFooter";
import CustomList from "../basic/custom/CustomList";
import CustomTextInput from "../basic/custom/CustomTextInput";

type SupplyFilterModalProps = {
  visible: boolean;
  onDismiss: () => void;
  onSearch: (params: Partial<SupplyQueryParams>) => void;
};

const typeOptions = Object.values(SupplyType).map((type) => ({
  label: type.charAt(0) + type.slice(1).toLowerCase(),
  key: type,
  value: type,
}));

export default function SupplyFilterModal({
  visible,
  onDismiss,
  onSearch,
}: SupplyFilterModalProps) {
  const { styles } = useThemedStyles();

  const [filters, setFilters] = useState({
    name: "",
    type: undefined,
  });

  const handleSearch = () => {
    onSearch({
      name: filters.name.trim() !== "" ? filters.name : undefined,
      type: filters.type || undefined,
    });
  };

  return (
    <Modal
      visible={visible}
      onDismiss={onDismiss}
      contentContainerStyle={styles.modalContainer}
    >
      <View style={styles.modalFilters}>
        <CustomTextInput
          textLabel="Supply Name"
          value={filters.name}
          onChangeText={(text) =>
            setFilters((prev) => ({ ...prev, name: text }))
          }
        />

        <CustomList
          list={typeOptions}
          selectedItem={filters.type}
          onSelect={(type) => setFilters((prev) => ({ ...prev, type }))}
          label={"Supply Type"}
        />

        <ModalFooter handleSearch={handleSearch} onDismiss={onDismiss} />
      </View>
    </Modal>
  );
}
