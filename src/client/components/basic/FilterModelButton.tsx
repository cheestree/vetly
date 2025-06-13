import colours from "@/theme/colours";
import size from "@/theme/size";
import spacing from "@/theme/spacing";
import { FontAwesome5 } from "@expo/vector-icons";
import { Pressable, StyleSheet } from "react-native";

type FilterModalButtonProps = {
  onPress: () => void;
};

export default function FilterModalButton({ onPress }: FilterModalButtonProps) {
  return (
    <Pressable onPress={onPress} style={style.filter}>
      <FontAwesome5 name="filter" size={size.icon.md} color="white" />
    </Pressable>
  );
}

const style = StyleSheet.create({
  filter: {
    position: "absolute",
    bottom: spacing.md,
    right: spacing.md,
    alignItems: "center",
    justifyContent: "center",
    width: 64,
    height: 64,
    borderRadius: size.border.xl,
    backgroundColor: colours.primary,
    zIndex: 10,
  },
});
