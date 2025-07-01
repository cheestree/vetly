import { ClinicQueryParams } from "@/api/clinic/clinic.input";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { useState } from "react";
import { View } from "react-native";
import { Modal } from "react-native-paper";
import ModalFooter from "../basic/base/ModalFooter";
import CustomTextInput from "../basic/custom/CustomTextInput";

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

  const [filters, setFilters] = useState({
    name: "",
    lat: "",
    lng: "",
  });

  const handleSearch = () => {
    const params: Partial<ClinicQueryParams> = {
      name: filters.name.trim() !== "" ? filters.name : undefined,
      lat:
        filters.lat.trim() !== "" && !isNaN(Number(filters.lat))
          ? Number(filters.lat)
          : undefined,
      lng:
        filters.lng.trim() !== "" && !isNaN(Number(filters.lng))
          ? Number(filters.lng)
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
      <View style={styles.modalContainer}>
        <View style={styles.modalFilters}>
          <CustomTextInput
            placeholder="Clinic Name"
            value={filters.name}
            onChangeText={(text) =>
              setFilters((prev) => ({ ...prev, name: text }))
            }
          />
          <CustomTextInput
            placeholder="Latitude"
            value={filters.lat}
            onChangeText={(text) =>
              setFilters((prev) => ({ ...prev, lat: text }))
            }
            keyboardType="numeric"
          />
          <CustomTextInput
            placeholder="Longitude"
            value={filters.lng}
            onChangeText={(text) =>
              setFilters((prev) => ({ ...prev, lng: text }))
            }
            keyboardType="numeric"
          />
        </View>

        <ModalFooter handleSearch={handleSearch} onDismiss={onDismiss} />
      </View>
    </Modal>
  );
}
