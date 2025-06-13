import layout from "@/theme/layout";
import { Text, View } from "react-native";
import CustomButton from "./CustomButton";

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
    <View style={layout.headerContainer}>
      <View>
        <Text style={layout.title}>{title}</Text>
        <Text style={layout.description}>{description}</Text>
      </View>
      <View>
        {buttons.map((button) => {
          return (
            <CustomButton
              key={button.name}
              onPress={button.operation}
              text={button.name}
              icon={button.icon}
            />
          );
        })}
      </View>
    </View>
  );
}
