import colours from "@/theme/colours";
import size from "@/theme/size";
import { FontAwesome5 } from "@expo/vector-icons";
import { Pressable, StyleSheet, Text, View } from "react-native";

type ButtonDescriptionProps = {
  name: string;
  icon: string;
  operation: () => void;
};

type PageHeaderProps = {
  title: string;
  description: string;
  buttons: ButtonDescriptionProps[];
};

export default function PageHeader({
  title,
  description,
  buttons,
}: PageHeaderProps) {
  return (
    <View style={style.headerContainer}>
      <View>
        <Text style={style.headerTitle}>{title}</Text>
        <Text style={style.headerDescription}>{description}</Text>
      </View>
      <View>
        {buttons.map((button) => {
          return (
            <Pressable
              key={button.name}
              onPress={button.operation}
              style={style.headerButton}
            >
              <FontAwesome5 name={button.icon} />
              <Text style={style.headerButtonText}>{button.name}</Text>
            </Pressable>
          );
        })}
      </View>
    </View>
  );
}

const style = StyleSheet.create({
  headerContainer: {
    marginTop: 24,
    marginBottom: 24,
    flexDirection: "row",
    justifyContent: "space-between",
    flexWrap: "wrap",
    verticalAlign: "middle",
  },
  headerDescription: {
    color: colours.fontDescription,
    fontSize: size.font.md,
  },
  headerTitle: {
    color: colours.fontHeader,
    fontSize: size.font.md,
    fontWeight: "bold",
  },
  headerButton: {
    backgroundColor: colours.primary,
    borderRadius: size.border.sm,
    flexDirection: "row",
    alignItems: "center",
    padding: size.padding.sm,
  },
  headerButtonText: {
    marginLeft: 20,
    color: colours.fontThirdiary,
  },
});
