import { SupplyQueryParams, SupplyType } from "@/api/supply/supply.input";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { useState } from "react";
import { View } from "react-native";
import { Modal } from "react-native-paper";
import CustomButton from "../basic/custom/CustomButton";
import CustomList from "../basic/custom/CustomList";
import CustomTextInput from "../basic/custom/CustomTextInput";

type Props = {
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
}: Props) {
  const { styles } = useThemedStyles();
  const [name, setName] = useState("");
  const [type, setType] = useState<SupplyType | undefined>(undefined);

  const handleSearch = () => {
    onSearch({
      name: name || undefined,
      type: type || undefined,
    });
  };

  return (
    <Modal
      visible={visible}
      onDismiss={onDismiss}
      contentContainerStyle={styles.modalContainer}
    >
      <View style={styles.modalContainer}>
        <CustomTextInput
          textLabel="Supply Name"
          value={name}
          onChangeText={setName}
        />

        <CustomList
          list={typeOptions}
          selectedItem={type}
          onSelect={setType}
          label={"Supply Type"}
        />

        <View style={styles.modalButtons}>
          <CustomButton onPress={handleSearch} text="Search" />

          <CustomButton onPress={onDismiss} text="Close" />
        </View>
      </View>
    </Modal>
  );
}
