import { FontAwesome, FontAwesome5 } from "@expo/vector-icons";
import { DrawerItem } from "@react-navigation/drawer";
import { useState } from "react";
import Utils from "@/lib/utils";
import { useRouter } from "expo-router";
import ROUTES from "@/lib/routes";

interface Props {
  roles: string[];
}

export default function AnimalSection({ roles }: Props) {
  const router = useRouter();
  const [expanded, setExpanded] = useState(false);

  return (
    <>
      <DrawerItem
        label="Animals"
        onPress={() => setExpanded((prev) => !prev)}
        icon={() => <FontAwesome5 name="paw" size={20} />}
      />
      {expanded && (
        <>
          {Utils.hasRole(roles, "VETERINARIAN", "ADMIN") && (
            <DrawerItem
              label="Search Animals"
              onPress={() => router.push(ROUTES.PRIVATE.ANIMAL.SEARCH)}
              style={{ marginLeft: 32 }}
            />
          )}
        </>
      )}
    </>
  );
}
