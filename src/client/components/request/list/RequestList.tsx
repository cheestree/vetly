import { RequestPreview } from "@/api/request/request.output";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { DataTable } from "react-native-paper";

type RequestListProps = {
  requests: RequestPreview[];
  onRowPress?: (request: RequestPreview) => void;
};

export default function RequestList({
  requests,
  onRowPress,
}: RequestListProps) {
  const { styles } = useThemedStyles();

  return (
    <DataTable style={styles.innerContainer}>
      <DataTable.Header>
        <DataTable.Title textStyle={styles.meta}>User</DataTable.Title>
        <DataTable.Title textStyle={styles.meta}>Action</DataTable.Title>
        <DataTable.Title textStyle={styles.meta}>Target</DataTable.Title>
        <DataTable.Title textStyle={styles.meta}>Status</DataTable.Title>
        <DataTable.Title textStyle={styles.meta}>Created</DataTable.Title>
      </DataTable.Header>
      {requests.map((request) => (
        <DataTable.Row
          key={request.id}
          onPress={onRowPress ? () => onRowPress(request) : undefined}
          style={onRowPress ? { cursor: "pointer" } : undefined}
        >
          <DataTable.Cell textStyle={styles.meta}>
            {request.user?.name ?? "—"}
          </DataTable.Cell>
          <DataTable.Cell textStyle={styles.meta}>
            {request.action}
          </DataTable.Cell>
          <DataTable.Cell textStyle={styles.meta}>
            {request.target}
          </DataTable.Cell>
          <DataTable.Cell textStyle={styles.meta}>
            {request.status}
          </DataTable.Cell>
          <DataTable.Cell textStyle={styles.meta}>
            {new Date(request.createdAt).toLocaleString()}
          </DataTable.Cell>
        </DataTable.Row>
      ))}
    </DataTable>
  );
}
