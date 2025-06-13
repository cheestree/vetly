import { StyleSheet } from "react-native";
import spacing from "../theme/spacing";
import colours from "./colours";
import size from "./size";

const layout = StyleSheet.create({
  title: {
    color: colours.fontHeader,
    fontSize: size.font.md,
    fontWeight: "bold",
  },
  description: {
    color: colours.fontDescription,
    fontSize: size.font.md,
  },
  container: {
    flex: 1,
    padding: spacing.md,
  },
  headerContainer: {
    marginTop: 24,
    marginBottom: 24,
    flexDirection: "row",
    justifyContent: "space-between",
    flexWrap: "wrap",
    verticalAlign: "middle",
  },
  modalContainer: {
    backgroundColor: "white",
    padding: spacing.md,
    justifyContent: "center",
    alignItems: "center",
  },
  button: {
    padding: size.padding.sm,
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
  imageContainer: {
    width: 200,
    height: 200,
    borderRadius: size.border.md,
    overflow: "hidden",
    backgroundColor: "#f0f0f0",
    justifyContent: "center",
    alignItems: "center",
  },
  image: {
    width: "100%",
    height: "100%",
    borderRadius: size.border.lg,
  },
});

export default layout;
