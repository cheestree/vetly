import { useThemedStyles } from "@/hooks/useThemedStyles";
import { StyleSheet, View } from "react-native";
import CustomButton from "../custom/CustomButton";

type ModalFooterProps = {
  handleSearch: () => void;
  onDismiss: () => void;
};

export default function ModalFooter({
  handleSearch,
  onDismiss,
}: ModalFooterProps) {
  const { styles } = useThemedStyles();

  return (
    <View style={styles.modalButtons}>
      <CustomButton
        onPress={handleSearch}
        stylePressable={extras.modalButtons}
        text="Search"
      />
      <CustomButton
        onPress={onDismiss}
        stylePressable={extras.modalButtons}
        text="Close"
      />
    </View>
  );
}

const extras = StyleSheet.create({
  modalButtons: {
    flex: 1,
  },
});
