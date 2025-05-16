import { useAuth } from "@/hooks/useAuth";
import React from "react";
import PrivateNavigator from "@/components/navigators/PrivateNavigator";
import PublicNavigator from "@/components/navigators/PublicNavigator";
import BaseComponent from "@/components/BaseComponent";

export default function Layout() {
  return <LayoutContent />;
}

function LayoutContent() {
  const { user } = useAuth();

  return (
    <BaseComponent isLoading={user != null}>
      {user ? <PrivateNavigator /> : <PublicNavigator />}
    </BaseComponent>
  );
}
