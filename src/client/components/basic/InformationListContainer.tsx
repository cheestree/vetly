import { useResource } from "@/hooks/useResource";
import { Text, View } from "react-native";

type InformationListContainerProps<T> = {
  loadItems: () => Promise<RequestList<T>>;
  renderItem: (item: T, index: number) => React.ReactNode;
  fallback?: React.ReactNode;
  loadingComponent?: React.ReactNode;
  itemLimit: number;
};

export default function InformationListContainer<T>({
  loadItems,
  renderItem,
  fallback = <Text>No items found</Text>,
  loadingComponent = <Text>Loading...</Text>,
  itemLimit = 3,
}: InformationListContainerProps<T>) {
  const { data: response, loading } = useResource<RequestList<T>>(loadItems);
  if (loading) return <>{loadingComponent}</>;

  if (!response || response.elements.length === 0) return <>{fallback}</>;

  return (
    <View>
      {response.elements
        .slice(0, itemLimit)
        .map((item, index) => renderItem(item, index))}
    </View>
  );
}
