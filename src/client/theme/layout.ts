import { Platform, StyleSheet } from "react-native";
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
    header: {
      fontSize: size.font.xl,
      fontWeight: "bold",
      marginBottom: size.margin.sm,
      color: colours.fontPrimary,
    },
    paragraph: {
      fontSize: size.font.md,
      marginBottom: size.margin.sm,
      color: colours.fontPrimary,
    },
    info: {
      fontSize: size.font.md,
      color: colours.fontSecondary,
    },
    description: {
      fontSize: size.font.md,
      color: colours.fontSecondary,
    },
    container: {
      flex: 1,
      paddingTop: size.padding.xl,
      paddingLeft: size.padding.xl,
      paddingRight: size.padding.xl,
      backgroundColor: colours.primaryBackground,
      ...getShadow(colours.shadowColor),
    },
    gridContainer: {
      flexDirection: "row",
      flexWrap: "wrap",
      gap: size.gap.xl,
      marginBottom: size.margin.xl,
    },
    listContainer: {
      flexWrap: "wrap",
      gap: size.gap.xl,
    },
    innerContainer: {
      borderRadius: size.border.md,
      padding: size.padding.xl,
      gap: size.gap.sm,
      backgroundColor: colours.secondaryBackground,
      ...getShadow(colours.secondaryBackgroundShadow),
    },
    miscContainer: {
      flex: 1,
    },
    floatingContainer: {
      position: "absolute",
      flexDirection: "column",
      bottom: size.margin.md,
      right: size.margin.md,
      gap: size.gap.md,
    },
    supplyContainer: {
      borderRadius: size.border.md,
      padding: size.padding.xl,
      backgroundColor: colours.secondaryBackground,
      ...getShadow(colours.secondaryBackgroundShadow),
    },
    bottomBar: {
      flexDirection: "row",
      justifyContent: "space-between",
      backgroundColor: colours.secondaryBackground,
      elevation: 2,
      ...getShadow(colours.secondaryBackgroundShadow),
    },
    bottomBarButton: {
      flex: 1,
      verticalAlign: "middle",
      alignItems: "center",
      padding: size.padding.md,
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
      marginBottom: size.margin.lg,
      gap: size.margin.lg,
      justifyContent: "space-between",
      flexWrap: "wrap",
      alignItems: "flex-start",
    },
    pageHeaderText: {
      minHeight: "auto",
      flexBasis: "auto",
      color: colours.fontPrimary,
      verticalAlign: "middle",
      justifyContent: "flex-end",
    },
    pageHeaderButtonsContainer: {
      flexDirection: "row",
      flexWrap: "wrap",
      flexShrink: 1,
      alignItems: "center",
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
      backgroundColor: colours.secondaryBackground,
      ...getShadow(colours.secondaryBackgroundShadow),
    },
    modalContainer: {
      borderRadius: size.border.md,
      padding: size.padding.md,
      justifyContent: "center",
      alignItems: "center",
      alignSelf: "center",
      backgroundColor: colours.secondaryBackground,
      gap: size.margin.md,
      ...getShadow(colours.shadowColor),
    },
    modalFilters: {
      width: "100%",
      gap: size.margin.md,
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
      gap: size.margin.md,
    },
    modalField: {
      flexDirection: "row",
      justifyContent: "space-between",
      borderRadius: size.border.md,
    },
    cardContainer: {
      gap: size.margin.lg,
      alignSelf: "flex-end",
      flexDirection: "column",
      borderRadius: size.border.md,
      padding: size.padding.lg,
      elevation: 3,
      backgroundColor: colours.cardBackground,
      ...getShadow(colours.shadowColor),
    },
    cardInfoContainer: {
      maxWidth: size.size.sm,
      gap: size.margin.lg,
    },
    cardButtonsContainer: {
      gap: size.margin.lg,
    },
    pagingFooter: {
      flexDirection: "row",
      justifyContent: "center",
      alignItems: "center",
      alignSelf: "center",
      gap: size.margin.md,
      backgroundColor: "transparent",
      position: "absolute",
      bottom: size.margin.md,
    },
    button: {
      flexDirection: "row",
      paddingVertical: size.padding.md,
      paddingHorizontal: size.padding.lg,
      alignItems: "center",
      justifyContent: "center",
      borderRadius: size.border.sm,
      gap: size.margin.sm,
      backgroundColor: colours.primary,
      color: colours.fontThirdiary,
      elevation: 2,
      ...getShadow(colours.shadowColor),
    },
    textInput: {
      borderRadius: size.border.sm,
      borderWidth: 1,
      borderColor: colours.border,
      paddingVertical: size.padding.md,
      paddingHorizontal: size.padding.lg,
      overflow: "hidden",
      backgroundColor: colours.primaryBackground,
      color: colours.fontPrimary,
      elevation: 2,
      ...getShadow(colours.primaryBackgroundShadow),
    },
    list: {
      backgroundColor: colours.primaryBackground,
      borderRadius: size.border.sm,
      borderColor: colours.border,
      maxHeight: 200,
      overflow: "hidden",
    },
    customListContainer: {
      borderRadius: size.border.sm,
      borderWidth: 1,
      borderColor: colours.border,
      marginBottom: size.margin.md,
      backgroundColor: colours.primaryBackground,
      ...getShadow(colours.primaryBackgroundShadow),
      elevation: 2,
    },
    customListLabel: {
      fontWeight: "bold",
      marginBottom: size.margin.xs,
      fontSize: size.font.md,
      color: colours.fontPrimary,
    },
    customListSelected: {
      flexDirection: "row",
      alignItems: "center",
      justifyContent: "space-between",
      padding: size.padding.md,
      borderRadius: size.border.sm,
      backgroundColor: colours.primaryBackground,
      marginBottom: size.margin.xs,
    },
    customListSelectedText: {
      fontSize: size.font.md,
      color: colours.fontPrimary,
    },
    customListList: {
      backgroundColor: colours.primaryBackground,
      borderRadius: size.border.sm,
      borderWidth: 1,
      borderColor: colours.border,
      maxHeight: 200,
      overflow: "hidden",
    },
    customListItem: {
      padding: size.padding.md,
    },
    customListSelectedItem: {
      backgroundColor: colours.secondaryBackground,
    },
    customListDisabledItem: {
      opacity: 0.5,
    },
    customListItemText: {
      fontSize: size.font.md,
      color: colours.fontPrimary,
    },
    customListSelectedItemText: {
      color: colours.fontPrimary,
      fontWeight: "bold",
    },
    filter: {
      alignItems: "center",
      justifyContent: "center",
      width: 64,
      height: 64,
      borderRadius: size.border.xl,
      zIndex: 10,
      backgroundColor: colours.primary,
      color: colours.fontThirdiary,
      ...getShadow(colours.shadowColor),
    },
    toggleButton: {
      borderRadius: size.border.sm,
      backgroundColor: colours.primaryBackground,
      ...getShadow(colours.primaryBackgroundShadow),
    },
    loader: {
      flex: 1,
      justifyContent: "center",
      alignItems: "center",
      backgroundColor: colours.secondaryBackground,
      ...getShadow(colours.secondaryBackgroundShadow),
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
      backgroundColor: colours.primaryBackground,
      ...getShadow(colours.primaryBackgroundShadow),
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

function getShadow(colour = "#000") {
  return Platform.select({
    web: {
      boxShadow: "0px 4px 10px rgba(0,0,0,0.15)",
    },
    default: {
      shadowColor: colour,
      shadowOffset: { width: 0, height: 4 },
      shadowOpacity: 0.2,
      shadowRadius: 6,
      elevation: 4,
    },
  });
}

export default layout;
