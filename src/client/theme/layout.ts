import { StyleSheet } from "react-native";
import spacing from "../theme/spacing";

const layout = StyleSheet.create({
  container: {
    flex: 1,
    padding: spacing.md,
  },
  row: {
    flexDirection: "row",
    alignItems: "center",
  },
  rowBetween: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },
  mb: {
    marginBottom: spacing.md,
  },
  mt: {
    marginTop: spacing.md,
  },
  ml: {
    marginLeft: spacing.md,
  },
  mr: {
    marginRight: spacing.md,
  },
  inputContainer: {
    maxWidth: 240,
    marginBottom: spacing.md,
    marginRight: spacing.md,
  },
});

export default layout;
