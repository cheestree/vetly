import { StyleSheet } from "react-native";
import spacing from "../theme/spacing";
import colours from "./colours";

const layout = StyleSheet.create({
  container: {
    flex: 1,
    padding: spacing.md,
  },
  baseButton: {
    color: colours.fontPrimary,
  },
});

export default layout;
