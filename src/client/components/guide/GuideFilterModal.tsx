import colours from "@/theme/colours"
import size from "@/theme/size"
import spacing from "@/theme/spacing"
import { useState } from "react"
import { View, StyleSheet } from "react-native"
import { Button, Modal } from "react-native-paper"

interface GuideFilterModalProps {
  visible: boolean
  onDismiss: () => void
  onSearch: (params: GuideQueryParams) => void
}

export default function GuideFilterModal({
  visible,
  onDismiss,
  onSearch
}: GuideFilterModalProps) {
  const [open, setOpen] = useState(false)

  const handleSearch = () => {
    const params: Partial<GuideQueryParams> = {

    }

    onSearch(params)
  }
  
  return (
    <Modal visible={visible} onDismiss={onDismiss}>
      <View style={styles.modalContainer}>
        <View style={styles.modalFilters}>
        </View>

        <View style={styles.modalButtons}>
          <Button style={styles.modalButton} onPress={handleSearch}>
            Search
          </Button>
          <Button style={styles.modalButton} onPress={onDismiss}>
            Close
          </Button>
        </View>
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
