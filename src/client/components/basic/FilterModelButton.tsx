import size from "@/theme/size";
import { FontAwesome5 } from "@expo/vector-icons";
import { Button } from "react-native-paper";
import { StyleSheet } from "react-native";
import colours from "@/theme/colours";
import spacing from "@/theme/spacing";

type FilterModalButtonProps = {
  onPress: () => void;
};

export default function FilterModalButton({ onPress }: FilterModalButtonProps) {
  return (
    <Button onPress={onPress} style={style.filter}>
      <FontAwesome5 name="filter" size={size.icon.md} color="white" />
    </Button>
  );
}

const style = StyleSheet.create({
  filter: {
    position: "absolute",
    bottom: spacing.md,
    right: spacing.md,
    justifyContent: "center",
    width: 64,
    height: 64,
    borderRadius: size.border.md,
    backgroundColor: colours.primary,
    zIndex: 10,
  },
});
