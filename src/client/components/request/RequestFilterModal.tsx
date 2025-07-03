import {
  actionOptions,
  RequestQueryParams,
  statusOptions,
  targetOptions,
  UserRequestQueryParams,
} from "@/api/request/request.input";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import size from "@/theme/size";
import { format } from "date-fns";
import { useCallback, useState } from "react";
import { View } from "react-native";
import { Modal } from "react-native-paper";
import ModalFooter from "../basic/base/ModalFooter";
import CustomList from "../basic/custom/CustomList";
import CustomTextInput from "../basic/custom/CustomTextInput";
import DateRangePicker from "../basic/DateRangePicker";

interface RequestFilterModalProps {
  visible: boolean;
  onDismiss: () => void;
  onSearch: (
    params: Partial<RequestQueryParams | UserRequestQueryParams>,
  ) => void;
  canSearchByUserId: boolean;
}

export default function RequestFilterModal({
  visible,
  onDismiss,
  onSearch,
  canSearchByUserId,
}: RequestFilterModalProps) {
  const { styles } = useThemedStyles();

  const [open, setOpen] = useState(false);
  const [filters, setFilters] = useState({
    userId: "",
    username: "",
    action: "",
    target: "",
    status: "",
    startDate: undefined as Date | undefined,
    endDate: undefined as Date | undefined,
  });

  const onDismissRange = useCallback(() => {
    setOpen(false);
  }, []);

  const onConfirmRange = useCallback(
    ({ startDate, endDate }: { startDate?: Date; endDate?: Date }) => {
      setOpen(false);
      setFilters((prev) => ({ ...prev, startDate, endDate }));
    },
    [],
  );

  const handleSearch = () => {
    const params: Partial<RequestQueryParams | UserRequestQueryParams> = {
      action: filters.action ? filters.action : undefined,
      target: filters.target ? filters.target : undefined,
      status: filters.status ? filters.status : undefined,
      submittedAfter: filters.startDate
        ? format(filters.startDate, "yyyy-MM-dd")
        : undefined,
      submittedBefore: filters.endDate
        ? format(filters.endDate, "yyyy-MM-dd")
        : undefined,
      userId:
        canSearchByUserId &&
        filters.userId.trim() !== "" &&
        !isNaN(Number(filters.userId))
          ? filters.userId.trim()
          : undefined,
      username:
        canSearchByUserId && filters.username.trim() !== ""
          ? filters.username
          : undefined,
    };

    onSearch(params);
  };

  return (
    <Modal
      visible={visible}
      onDismiss={onDismiss}
      contentContainerStyle={styles.modalContainer}
    >
      <View style={styles.modalFilters}>
        <View style={{ alignItems: "center", gap: size.gap.md }}>
          <DateRangePicker
            startDate={filters.startDate}
            endDate={filters.endDate}
            onChange={({ startDate, endDate }) =>
              setFilters((prev) => ({ ...prev, startDate, endDate }))
            }
          />
        </View>

        <CustomList
          list={actionOptions}
          selectedItem={filters.action}
          onSelect={(value) =>
            setFilters((prev) => ({ ...prev, action: value }))
          }
        />

        <CustomList
          list={targetOptions}
          selectedItem={filters.target}
          onSelect={(value) =>
            setFilters((prev) => ({ ...prev, target: value }))
          }
        />

        <CustomList
          list={statusOptions}
          selectedItem={filters.status}
          onSelect={(value) =>
            setFilters((prev) => ({ ...prev, status: value }))
          }
        />

        {canSearchByUserId && (
          <>
            <CustomTextInput
              placeholder="User ID"
              value={filters.userId}
              onChangeText={(text) =>
                setFilters((prev) => ({ ...prev, userId: text }))
              }
              keyboardType="numeric"
            />
            <CustomTextInput
              placeholder="Username"
              value={filters.username}
              onChangeText={(text) =>
                setFilters((prev) => ({ ...prev, username: text }))
              }
            />
          </>
        )}

        <ModalFooter handleSearch={handleSearch} onDismiss={onDismiss} />
      </View>
    </Modal>
  );
}
