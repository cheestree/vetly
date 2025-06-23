import {
  RequestQueryParams,
  UserRequestQueryParams,
} from "@/api/request/request.input";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { format } from "date-fns";
import { useCallback, useState } from "react";
import { View } from "react-native";
import { Modal } from "react-native-paper";
import { DatePickerModal } from "react-native-paper-dates";
import CustomButton from "../basic/custom/CustomButton";
import CustomText from "../basic/custom/CustomText";
import CustomTextInput from "../basic/custom/CustomTextInput";

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
  const [range, setRange] = useState<{ startDate?: Date; endDate?: Date }>({
    startDate: undefined,
    endDate: undefined,
  });

  const [userId, setUserId] = useState("");
  const [username, setUsername] = useState("");
  const [action, setAction] = useState("");
  const [target, setTarget] = useState("");
  const [status, setStatus] = useState("");

  const onDismissRange = useCallback(() => {
    setOpen(false);
  }, [setOpen]);

  const onConfirmRange = useCallback(
    ({ startDate, endDate }: { startDate?: Date; endDate?: Date }) => {
      setOpen(false);
      setRange({ startDate, endDate });
    },
    [setOpen, setRange],
  );

  const handleSearch = () => {
    const params: Partial<RequestQueryParams | UserRequestQueryParams> = {
      action: action || undefined,
      target: target || undefined,
      status: status || undefined,
      submittedAfter: range.startDate
        ? range.startDate.toISOString()
        : undefined,
      submittedBefore: range.endDate ? range.endDate.toISOString() : undefined,
      userId:
        canSearchByUserId && userId.trim() !== "" ? Number(userId) : undefined,
      username:
        canSearchByUserId && username.trim() !== "" ? username : undefined,
    };

    onSearch(params);
  };

  return (
    <Modal
      visible={visible}
      onDismiss={onDismiss}
      contentContainerStyle={styles.modalContainer}
    >
      <View style={styles.modalContainer}>
        <View style={styles.modalFilters}>
          <View>
            <CustomButton
              onPress={() => setOpen(true)}
              text="Pick submitted date range"
            />
            <DatePickerModal
              locale="en"
              mode="range"
              visible={open}
              onDismiss={onDismissRange}
              startDate={range.startDate}
              endDate={range.endDate}
              onConfirm={onConfirmRange}
            />
            {range.startDate && range.endDate && (
              <CustomText
                text={`${format(range.startDate, "MMM d, yyyy")} - ${format(range.endDate, "MMM d, yyyy")}`}
              />
            )}
          </View>

          <CustomTextInput
            placeholder="Action"
            value={action}
            onChangeText={setAction}
          />
          <CustomTextInput
            placeholder="Target"
            value={target}
            onChangeText={setTarget}
          />
          <CustomTextInput
            placeholder="Status"
            value={status}
            onChangeText={setStatus}
          />

          {canSearchByUserId && (
            <>
              <CustomTextInput
                placeholder="User ID"
                value={userId}
                onChangeText={setUserId}
                keyboardType="numeric"
              />
              <CustomTextInput
                placeholder="Username"
                value={username}
                onChangeText={setUsername}
              />
            </>
          )}

          <View style={styles.modalButtons}>
            <CustomButton onPress={handleSearch} text="Search" />
            <CustomButton onPress={onDismiss} text="Close" />
          </View>
        </View>
      </View>
    </Modal>
  );
}
