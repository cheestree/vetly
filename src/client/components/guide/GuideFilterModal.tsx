import layout from "@/theme/layout";
import spacing from "@/theme/spacing";
import { Pressable, StyleSheet, Text, View } from "react-native";
import { Modal } from "react-native-paper";

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
  const handleSearch = () => {
    const params: Partial<GuideQueryParams> = {};

    onSearch(params);
  };

  return (
    <Modal
      visible={visible}
      onDismiss={onDismiss}
      contentContainerStyle={layout.modalContainer}
    >
      <View style={layout.modalContainer}>
        <View style={styles.modalFilters}></View>

        <View style={styles.modalButtons}>
          <Pressable style={layout.button} onPress={handleSearch}>
            <Text style={layout.buttonText}>Search</Text>
          </Pressable>
          <Pressable style={layout.button} onPress={onDismiss}>
            <Text style={layout.buttonText}>Close</Text>
          </Pressable>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
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
