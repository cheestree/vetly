import { DrawerItem } from "@react-navigation/drawer";
import { useRouter } from "expo-router";
import Utils from "@/lib/utils";
import { FontAwesome } from "@expo/vector-icons";
import { useState } from "react";
import ROUTES from "@/lib/routes";

interface Props {
  roles: string[];
}

export default function ClinicSection({ roles }: Props) {
  const router = useRouter();
  const [expanded, setExpanded] = useState(false);

  return (
    <>
      <DrawerItem
        label="Clinics"
        onPress={() => setExpanded((prev) => !prev)}
        icon={() => <FontAwesome name="hospital-o" size={20} />}
      />
      {expanded && (
        <>
          <DrawerItem
            label="Search Clinics"
            onPress={() => router.push(ROUTES.PRIVATE.CLINIC.SEARCH)}
            style={{ marginLeft: 32 }}
          />
        </>
      )}
    </>
  );
}
