import { useAuth } from "@/hooks/useAuth";
import React from "react";
import PublicNavigator from "@/components/navigators/public/PublicNavigator";
import BaseComponent from "@/components/BaseComponent";

export default function Layout() {
  return <LayoutContent />;
}

function LayoutContent() {
  const { user } = useAuth();

  return (
    <BaseComponent isLoading={user != null}>
      <PublicNavigator />
    </BaseComponent>
  );
}
