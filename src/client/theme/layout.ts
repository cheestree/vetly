import { StyleSheet } from "react-native";
import spacing from "../theme/spacing";
import colours from "./colours";
import size from "./size";

const layout = StyleSheet.create({
  container: {
    flex: 1,
    padding: spacing.md,
  },
  modalContainer: {
    backgroundColor: "white",
    padding: spacing.md,
    justifyContent: "center",
    alignItems: "center",
  },
  button: {
    padding: size.padding.md,
    alignSelf: "center",
    alignItems: "center",
    marginTop: 8,
    backgroundColor: colours.primary,
    borderRadius: 6,
    width: "100%",
  },
  buttonText: {
    color: colours.fontThirdiary,
  },
});

export default layout;
