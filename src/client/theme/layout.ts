import { StyleSheet } from "react-native";
import spacing from "../theme/spacing";
import { ColorScheme } from "./colours";
import size from "./size";

const layout = (colours: ColorScheme) => {
  return StyleSheet.create({
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
      backgroundColor: colours.primaryBackground,
    },
    pageHeaderContainer: {
      marginTop: spacing.lg,
      marginBottom: spacing.lg,
      flexDirection: "row",
      justifyContent: "space-between",
      flexWrap: "wrap",
      verticalAlign: "middle",
    },
    headerContainer: {
      flexDirection: "row",
      alignItems: "center",
      borderBottomWidth: 1,
      borderBottomColor: colours.border,
      backgroundColor: colours.secondaryBackground,
      width: "100%",
      height: 63,
      paddingHorizontal: 12,
    },
    modalContainer: {
      backgroundColor: colours.cardBackground,
      padding: spacing.md,
      justifyContent: "center",
      alignItems: "center",
    },
    button: {
      padding: size.padding.sm,
      alignSelf: "center",
      alignItems: "center",
      marginTop: spacing.sm,
      backgroundColor: colours.primary,
      borderRadius: size.border.sm,
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
    loader: {
      flex: 1,
      backgroundColor: colours.secondaryBackground,
      justifyContent: "center",
      alignItems: "center",
    },
    label: {
      fontSize: size.font.md,
      marginRight: spacing.sm,
    },
    imageContainer: {
      width: 200,
      height: 200,
      borderRadius: size.border.md,
      overflow: "hidden",
      backgroundColor: colours.primaryBackground,
      justifyContent: "center",
      alignItems: "center",
    },
    image: {
      width: "100%",
      height: "100%",
      borderRadius: size.border.lg,
    },
  });
};

export default layout;
