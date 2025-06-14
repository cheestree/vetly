import { useThemedStyles } from "@/hooks/useThemedStyles";
import spacing from "@/theme/spacing";
import { Pressable, StyleSheet, Text, View } from "react-native";
import { Modal } from "react-native-paper";

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
        <View style={extra.modalFilters}></View>

        <View style={extra.modalButtons}>
          <Pressable style={styles.button} onPress={handleSearch}>
            <Text style={styles.buttonText}>Search</Text>
          </Pressable>
          <Pressable style={styles.button} onPress={onDismiss}>
            <Text style={styles.buttonText}>Close</Text>
          </Pressable>
        </View>
      </View>
    </Modal>
  );
}

const extra = StyleSheet.create({
  modalFilters: {
    width: "100%",
    gap: spacing.md,
    marginBottom: spacing.md,
  },
  modalButtons: {
    flexDirection: "row",
    justifyContent: "space-around",
    width: "100%",
  },
  rangeText: {
    marginLeft: spacing.sm,
    fontSize: 14,
    color: "gray",
  },
});
