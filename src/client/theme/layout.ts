import { StyleSheet } from "react-native";
import { ColorScheme } from "./colours";
import size from "./size";

type LayoutProps = {
  colours: ColorScheme;
};

const layout = ({ colours }: LayoutProps) => {
  return StyleSheet.create({
    title: {
      fontSize: size.font.md,
      fontWeight: "bold",
      color: colours.fontHeader,
    },
    description: {
      fontSize: size.font.md,
      color: colours.fontDescription,
    },
    container: {
      flex: 1,
      padding: size.padding.xl,
      shadowColor: colours.secondaryBackgroundShadow,
      backgroundColor: colours.secondaryBackground,
    },
    innerContainer: {
      borderRadius: size.border.md,
      padding: size.padding.xl,
      gap: size.gap.sm,
      shadowColor: colours.thirdiaryBackgroundShadow,
      backgroundColor: colours.thirdiaryBackground,
    },
    miscContainer: {
      flex: 1,
    },
    drawerTop: {
      flexDirection: "row",
      alignItems: "center",
      borderBottomWidth: 1,
      borderBottomColor: "#eee",
      padding: size.padding.md,
    },
    drawerItem: {
      marginVertical: size.margin.xs,
      overflow: "hidden",
      borderRadius: size.border.xs,
      padding: size.padding.md,
      flexDirection: "row",
      alignItems: "center",
      gap: size.gap.sm,
    },
    pageHeaderContainer: {
      marginTop: size.margin.lg,
      marginBottom: size.margin.lg,
      flexDirection: "row",
      justifyContent: "space-between",
      flexWrap: "wrap",
      alignItems: "flex-start",
    },
    pageHeaderText: {
      flex: 1,
      verticalAlign: "middle",
      justifyContent: "flex-end",
    },
    pageHeaderButtonsContainer: {
      flexDirection: "row",
      flexShrink: 1,
      justifyContent: "flex-end",
      minWidth: "auto",
      gap: size.margin.lg,
    },
    headerContainer: {
      flexDirection: "row",
      alignItems: "center",
      borderBottomWidth: 1,
      width: "100%",
      height: 63,
      paddingHorizontal: size.padding.sm,
      borderBottomColor: colours.border,
      shadowColor: colours.secondaryBackgroundShadow,
      backgroundColor: colours.secondaryBackground,
    },
    modalContainer: {
      borderRadius: size.border.md,
      padding: size.padding.md,
      justifyContent: "center",
      alignItems: "center",
      alignSelf: "center",
      shadowColor: colours.secondaryBackgroundShadow,
      backgroundColor: colours.secondaryBackground,
    },
    modalFilters: {
      width: "100%",
      gap: size.margin.md,
      marginBottom: size.margin.md,
    },
    modalRangeText: {
      marginLeft: size.margin.sm,
      fontSize: 14,
      color: "gray",
    },
    modalButtons: {
      flexDirection: "row",
      justifyContent: "space-around",
      width: "100%",
    },
    modalField: {
      flexDirection: "row",
      justifyContent: "space-between",
      borderRadius: size.border.md,
    },
    cardContainer: {
      height: size.height.xl,
      gap: size.margin.lg,
      alignSelf: "flex-end",
      flexDirection: "column",
      borderRadius: size.border.md,
      padding: size.padding.lg,
      elevation: 3,
      boxShadow: "0px 2px 6px rgba(0, 0, 0, 0.08)",
      backgroundColor: colours.cardBackground,
    },
    cardImageContainer: {},
    cardInfoContainer: {
      maxWidth: size.size.sm,
      gap: size.margin.lg,
    },
    cardButtonsContainer: {
      gap: size.margin.lg,
    },
    button: {
      flexDirection: "row",
      paddingVertical: size.padding.sm,
      paddingHorizontal: size.padding.md,
      alignItems: "center",
      justifyContent: "center",
      borderRadius: size.border.sm,
      gap: size.margin.sm,
      shadowColor: colours.cardBackgroundShadow,
      backgroundColor: colours.cardBackground,
    },
    textInput: {
      borderRadius: size.border.sm,
      borderWidth: 1,
      borderColor: colours.border,
      padding: size.padding.xl,
      overflow: "hidden",
      shadowColor: colours.primaryBackgroundShadow,
      backgroundColor: colours.primaryBackground,
    },
    filter: {
      position: "absolute",
      bottom: size.margin.md,
      right: size.margin.md,
      alignItems: "center",
      justifyContent: "center",
      width: 64,
      height: 64,
      borderRadius: size.border.xl,
      zIndex: 10,
      shadowColor: colours.primaryBackgroundShadow,
      backgroundColor: colours.primaryBackground,
    },
    toggleButton: {
      borderRadius: size.border.sm,
      shadowColor: colours.primaryBackgroundShadow,
      backgroundColor: colours.primaryBackground,
    },
    loader: {
      flex: 1,
      justifyContent: "center",
      alignItems: "center",
      shadowColor: colours.secondaryBackgroundShadow,
      backgroundColor: colours.secondaryBackground,
    },
    label: {
      fontSize: size.font.md,
      marginRight: size.margin.sm,
    },
    imageContainer: {
      width: size.size.sm,
      height: size.size.sm,
      borderRadius: size.border.md,
      overflow: "hidden",
      justifyContent: "center",
      alignItems: "center",
      shadowColor: colours.primaryBackgroundShadow,
      backgroundColor: colours.primaryBackground,
    },
    image: {
      width: size.size.sm,
      height: size.size.sm,
      borderRadius: size.border.sm,
    },
    metaContainer: {
      flexDirection: "row",
      alignItems: "center",
    },
    icon: {
      marginRight: size.margin.sm,
      color: colours.iconColour,
    },
    meta: {
      fontSize: size.font.md,
      flexShrink: 1,
      color: colours.fontPrimary,
    },
  });
};

export default layout;
