import { useThemedStyles } from "@/hooks/useThemedStyles";
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
  const style = useThemedStyles();

  const handleSearch = () => {
    const params: Partial<GuideQueryParams> = {};

    onSearch(params);
  };

  return (
    <Modal
      visible={visible}
      onDismiss={onDismiss}
      contentContainerStyle={style.modalContainer}
    >
      <View style={style.modalContainer}>
        <View style={extra.modalFilters}></View>

        <View style={extra.modalButtons}>
          <Pressable style={style.button} onPress={handleSearch}>
            <Text style={style.buttonText}>Search</Text>
          </Pressable>
          <Pressable style={style.button} onPress={onDismiss}>
            <Text style={style.buttonText}>Close</Text>
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
