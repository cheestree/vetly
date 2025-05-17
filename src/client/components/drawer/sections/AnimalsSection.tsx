import { FontAwesome } from "@expo/vector-icons";
import { DrawerItem } from "@react-navigation/drawer";
import { Router } from "expo-router";
import { useState } from "react";
import Utils from '@/lib/utils'

interface Props {
  router: Router;
  roles: string[];
}

export default function AnimalsSection({ router, roles }: Props) {
  const [expanded, setExpanded] = useState(false);

  return (
    <>
      <DrawerItem
        label="Animals"
        onPress={() => setExpanded((prev) => !prev)}
        icon={() => <FontAwesome name="search" size={20} />}
      />
      {expanded && (
        <>
          <DrawerItem
            label="My Pets"
            onPress={() => router.navigate("/(private)/(drawer)/me/animals")}
            style={{ marginLeft: 32 }}
          />
          {Utils.hasRole(roles, 'VETERINARIAN', 'ADMIN') &&
            <DrawerItem
              label="Search Animals"
              onPress={() => router.navigate("/(private)/(drawer)/animal")}
              style={{ marginLeft: 32 }}
            />
          }
        </>
      )}
    </>
  );
}
