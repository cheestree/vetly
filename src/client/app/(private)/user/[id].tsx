import userApi from "@/api/user/user.api";
import { UserInformation } from "@/api/user/user.output";
import BaseComponent from "@/components/basic/base/BaseComponent";
import UserDetailsContent from "@/components/user/UserDetailsContent";
import { useResource } from "@/hooks/useResource";
import { Stack, useFocusEffect, useLocalSearchParams } from "expo-router";
import { useCallback } from "react";

export default function UserDetails() {
  const { id } = useLocalSearchParams();

  const fetchUser = useCallback(() => userApi.getUser(id), [id]);

  const {
    data: user,
    loading,
    refetch,
  } = useResource<UserInformation>(fetchUser);

  useFocusEffect(
    useCallback(() => {
      refetch();
    }, [refetch]),
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
