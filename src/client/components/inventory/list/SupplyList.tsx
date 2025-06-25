import { SupplyPreview } from "@/api/supply/supply.output";
import { ScrollView, StyleSheet, View } from "react-native";
import CustomText from "../../basic/custom/CustomText";

type SupplyListProps = {
  supplies: SupplyPreview[];
};

export default function SupplyList({ supplies }: SupplyListProps) {
  return (
    <ScrollView>
      {supplies.map((supply) => (
        <View key={supply.id} style={styles.card}>
          <CustomText text={supply.name} />

          <CustomText text={`Type: ${supply.type}`} />
        </View>
      ))}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  card: {
    padding: 12,
    margin: 8,
    backgroundColor: "#f3f4f6",
    borderRadius: 8,
  },
});
