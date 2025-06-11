import colours from "@/theme/colours";
import size from "@/theme/size";
import spacing from "@/theme/spacing";
import { useState } from "react";
import { View, StyleSheet } from "react-native";
import { Button, Modal } from "react-native-paper";

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
  const handleSearch = () => {
    const params: Partial<ClinicQueryParams> = {};

    onSearch(params);
  };

  return (
    <Modal visible={visible} onDismiss={onDismiss} contentContainerStyle={styles.modalContainer}>
      <View style={styles.modalFilters}></View>

      <View style={styles.modalButtons}>
        <Button style={styles.modalButton} onPress={handleSearch}>
          Search
        </Button>
        <Button style={styles.modalButton} onPress={onDismiss}>
          Close
        </Button>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  modalContainer: {
    flex: 1,
    backgroundColor: "white",
    padding: spacing.md,
    justifyContent: "center",
    alignItems: "center",
  },
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
  modalButton: {
    flex: 1,
    marginHorizontal: spacing.sm,
    backgroundColor: colours.primary,
    borderRadius: size.border.sm,
  },
  rangeText: {
    marginLeft: spacing.sm,
    fontSize: 14,
    color: "gray",
  },
});
