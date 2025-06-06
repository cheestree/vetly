import ROUTES from "@/lib/routes";
import { FontAwesome, FontAwesome5 } from "@expo/vector-icons";
import { DrawerItem } from "@react-navigation/drawer";
import { useRouter } from "expo-router";
import { useState } from "react";

interface Props {
  roles: string[];
}

export default function CheckupSection({ roles }: Props) {
  const router = useRouter();
  const [expanded, setExpanded] = useState(false);

  return (
    <>
      <DrawerItem
        label="Checkups"
        onPress={() => setExpanded((prev) => !prev)}
        icon={() => <FontAwesome5 name="star-of-life" size={20} />}
      />
      {expanded && (
        <>
          <DrawerItem
            label="Search Checkups"
            onPress={() => router.push(ROUTES.PRIVATE.CHECKUP.SEARCH)}
            style={{ marginLeft: 32 }}
          />
        </>
      )}
    </>
  );
}
