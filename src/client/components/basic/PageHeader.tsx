import colours from "@/theme/colours";
import { FontAwesome5 } from "@expo/vector-icons";
import { Text, StyleSheet, View } from "react-native";
import { Button } from "react-native-paper";

type ButtonDescriptionProps = {
  name: string;
  icon: string;
  operation: () => void;
};

type PageHeader = {
  title: string;
  description: string;
  buttons: ButtonDescriptionProps[];
};

export default function PageHeader({
  title,
  description,
  buttons,
}: PageHeader) {
  return (
    <View style={style.headerContainer}>
      <View style={style.headerText}>
        <Text style={style.headerTitle}>{title}</Text>
        <Text style={style.headerDescription}>{description}</Text>
      </View>
      <View>
        {buttons.map((button) => {
          return (
            <Button key={button.name} onPress={button.operation} style={style.headerButton}>
              <FontAwesome5 name={button.icon}/><Text style={style.headerButtonText}>{button.name}</Text>
            </Button>
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
    flexWrap: 'wrap',
    verticalAlign: "middle",
  },
  headerText: {
    wordWrap: ''
  },
  headerDescription: {
    color: colours.fontSecondary,
    fontSize: 16,
  },
  headerTitle: {
    color: colours.fontPrimary,
    fontSize: 20,
    fontWeight: "bold"
  },
  headerButton: {
    backgroundColor: colours.primary,
    borderRadius: 6
  },
  headerButtonText: {
    marginLeft: 20,
    color: colours.fontThirdiary,
  }
});
