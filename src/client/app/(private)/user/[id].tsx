import userApi from "@/api/user/user.api";
import BaseComponent from "@/components/basic/base/BaseComponent";
import UserDetailsContent from "@/components/user/UserDetailsContent";
import { useResource } from "@/hooks/useResource";
import { Stack, useLocalSearchParams } from "expo-router";

export default function UserDetails() {
  const { id } = useLocalSearchParams();

  const { data: user, loading } = useResource<UserInformation>(() =>
    userApi.getUser(id as string),
  );

  return (
    <>
      <Stack.Screen options={{ title: user?.name }} />
      <BaseComponent isLoading={loading} title={user?.name || "User Details"}>
        <UserDetailsContent user={user} />
      </BaseComponent>
    </>
  );
}
