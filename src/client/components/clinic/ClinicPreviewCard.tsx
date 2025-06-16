import { useThemedStyles } from "@/hooks/useThemedStyles";
import ROUTES from "@/lib/routes";
import size from "@/theme/size";
import { useRouter } from "expo-router";
import { Pressable, StyleSheet, View } from "react-native";
import SafeImage from "../basic/SafeImage";
import CustomText from "../basic/custom/CustomText";

interface ClinicPreviewCardProps {
  clinic: ClinicPreview;
}

export default function ClinicPreviewCard({ clinic }: ClinicPreviewCardProps) {
  const { colours, styles } = useThemedStyles();
  const router = useRouter();

  const maxVisibleServices = 1;
  const visibleServices = clinic.services.slice(0, maxVisibleServices);
  const remainingCount = clinic.services.length - maxVisibleServices;

  const extras = StyleSheet.create({
    cardServiceContainer: {
      flexDirection: "row",
      gap: size.gap.sm,
      flexWrap: "wrap",
    },
    serviceLabel: {
      borderRadius: size.border.xs,
      backgroundColor: colours.secondaryBackground,
      paddingHorizontal: size.padding.sm,
      paddingVertical: size.padding.xs,
    },
  });
  return (
    <Pressable
      onPress={() =>
        router.navigate({
          pathname: ROUTES.PRIVATE.CLINIC.DETAILS,
          params: { id: clinic.id },
        })
      }
      style={styles.cardContainer}
    >
      <View style={styles.cardImageContainer}>
        <SafeImage
          uri={clinic.imageUrl}
          fallback={require("@/assets/placeholder.png")}
          style={styles.image}
          resizeMode="cover"
        />
      </View>

      <View style={styles.cardInfoContainer}>
        <CustomText text={clinic.name} />
        <CustomText icon={"map-pin"} text={clinic.address} />
        <CustomText icon={"phone"} text={clinic.phone} />
        <View style={extras.cardServiceContainer}>
          {clinic.services.length > 0 ? (
            <>
              {visibleServices.map((service, index) => (
                <View key={index} style={extras.serviceLabel}>
                  <CustomText text={service.toString()} />
                </View>
              ))}
              {remainingCount > 0 && (
                <View style={extras.serviceLabel}>
                  <CustomText text={`+${remainingCount} more`} />
                </View>
              )}
            </>
          ) : (
            <CustomText text="None listed" />
          )}
        </View>
      </View>
    </Pressable>
  );
}
