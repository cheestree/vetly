import userApi from "@/api/user/user.api";
import { UserInformation } from "@/api/user/user.output";
import BaseComponent from "@/components/basic/base/BaseComponent";
import UserDetailsContent from "@/components/user/UserDetailsContent";
import { useResource } from "@/hooks/useResource";
import { useFocusEffect, useLocalSearchParams } from "expo-router";
import { useCallback } from "react";

export default function UserDetails() {
  const { id } = useLocalSearchParams();
  const stringId = String(id);

  const fetchUser = useCallback(() => userApi.getUser(stringId), [stringId]);

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
    <BaseComponent isLoading={loading} title={user?.name || "User Details"}>
      <UserDetailsContent user={user} />
    </BaseComponent>
  );
}
