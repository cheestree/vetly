import { StyleSheet } from "react-native";
import { ColorScheme } from "./colours";
import size from "./size";

type LayoutProps = {
  colours: ColorScheme;
};

const layout = ({ colours }: LayoutProps) => {
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
      padding: size.padding.xl,
      backgroundColor: colours.secondaryBackground,
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
      borderBottomColor: colours.border,
      backgroundColor: colours.secondaryBackground,
      width: "100%",
      height: 63,
      paddingHorizontal: size.padding.sm,
    },
    modalContainer: {
      backgroundColor: colours.secondaryBackground,
      borderRadius: size.border.md,
      padding: size.padding.md,
      justifyContent: "center",
      alignItems: "center",
      alignSelf: "center",
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
      backgroundColor: colours.cardBackground,
      borderRadius: size.border.md,
      padding: size.padding.lg,
      boxShadow: "0px 2px 6px rgba(0, 0, 0, 0.08)",
      elevation: 3,
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
      backgroundColor: colours.thirdiaryBackground,
      borderRadius: size.border.sm,
    },
    textInput: {
      backgroundColor: colours.primaryBackground,
      borderRadius: size.border.sm,
      borderWidth: 1,
      borderColor: colours.border,
      padding: size.padding.xl,
      overflow: "hidden",
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
      backgroundColor: colours.primaryBackground,
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
      marginRight: size.margin.sm,
    },
    imageContainer: {
      width: size.size.sm,
      height: size.size.sm,
      borderRadius: size.border.md,
      overflow: "hidden",
      backgroundColor: colours.primaryBackground,
      justifyContent: "center",
      alignItems: "center",
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
      color: colours.iconColour,
      marginRight: size.margin.sm,
    },
    meta: {
      fontSize: size.font.md,
      color: colours.fontPrimary,
      flexShrink: 1,
    },
  });
};

export default layout;
