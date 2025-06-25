import {
  LiquidSupplyInformation,
  PillSupplyInformation,
  ShotSupplyInformation,
  SupplyInformation,
} from "@/api/supply/supply.output";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { View } from "react-native";
import CustomText from "../basic/custom/CustomText";

type Props = {
  supply?: SupplyInformation & { type?: string };
};

export default function SupplyDetailsContent({ supply }: Props) {
  const { styles } = useThemedStyles();
  if (!supply) {
    return <View style={styles.container}></View>;
  }

  return (
    <View style={styles.container}>
      <CustomText text={`Name: ${supply.name}`} />
      <CustomText text={`Type: ${supply.type ?? "Unknown"}`} />
      {supply.description && <CustomText text={supply.description} />}
      {/* Render subtype-specific fields */}
      {supply.type === "shot" && (
        <>
          <CustomText
            text={`Vials per box: ${(supply as ShotSupplyInformation).vialsPerBox}`}
          />
          <CustomText
            text={`ml per vial: ${(supply as ShotSupplyInformation).mlPerVial}`}
          />
        </>
      )}
      {supply.type === "liquid" && (
        <>
          <CustomText
            text={`ml per bottle: ${(supply as LiquidSupplyInformation).mlPerBottle}`}
          />
          <CustomText
            text={`ml dose per use: ${(supply as LiquidSupplyInformation).mlDosePerUse}`}
          />
        </>
      )}
      {supply.type === "pill" && (
        <>
          <CustomText
            text={`Pills per box: ${(supply as PillSupplyInformation).pillsPerBox}`}
          />
          <CustomText
            text={`mg per pill: ${(supply as PillSupplyInformation).mgPerPill}`}
          />
        </>
      )}
    </View>
  );
}
