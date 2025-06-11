import { useResource } from "@/hooks/useResource";
import { Text, View } from 'react-native';

type InformationContainerProps<T> = {
  loadItems: () => Promise<RequestList<T>>
  renderItem?: (item: T, index: number) => React.ReactNode
  label: string
  trend?: number
  fallback?: React.ReactNode
  loadingComponent?: React.ReactNode
  itemLimit?: number
}

export default function InformationContainer<T>({
  loadItems,
  renderItem,
  label,
  trend = 0,
  fallback = <Text>No items found</Text>,
  loadingComponent = <Text>Loading...</Text>,
  itemLimit = 3
}: InformationContainerProps<T>) {
  const { data: response, loading } = useResource<RequestList<T>>(loadItems)

  if (loading) return <>{loadingComponent}</>

  if (!response || response.elements.length === 0) return <>{fallback}</>

  const trendText =
    trend !== undefined
      ? trend === 0
        ? 'No change from yesterday'
        : `${trend > 0 ? '+' : ''}${trend} compared to yesterday`
      : null

  return (
    <View style={{ marginBottom: 24 }}>
      <Text style={{ fontSize: 18, fontWeight: 'bold' }}>
        {response.elements.length} {label}
      </Text>
      {trendText && (
        <Text style={{ color: trend > 0 ? 'green' : trend < 0 ? 'red' : 'gray' }}>
          {trendText}
        </Text>
      )}

      {renderItem && response.elements.slice(0, itemLimit).map((item, index) =>
        renderItem(item, index)
      )}
    </View>
  )
}