import { Text, StyleSheet, View } from "react-native";
import { Button } from "react-native-paper";

type ButtonDescriptionProps = {
  name: string;
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
      <View>
        {title}
        <Text>{description}</Text>
      </View>
      <View>
        {buttons.map((button) => {
          return (
            <Button onPress={button.operation}>
              <Text>{button.name}</Text>
            </Button>
          );
        })}
      </View>
    </View>
  );
}

const style = StyleSheet.create({
  headerContainer: {
    flexDirection: "row",
    justifyContent: "space-between",
    verticalAlign: "middle",
  },
});
