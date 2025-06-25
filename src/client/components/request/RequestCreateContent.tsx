import {
  RequestAction,
  RequestCreate,
  RequestTarget,
} from "@/api/request/request.input";
import { useState } from "react";
import { Alert, View } from "react-native";
import CustomButton from "../basic/custom/CustomButton";
import CustomList from "../basic/custom/CustomList";
import CustomTextInput from "../basic/custom/CustomTextInput";
import ClinicCreateForm from "./forms/ClinicCreateForm";
import ClinicMembershipCreateForm from "./forms/ClinicMembershipCreateForm";
import RoleUpdateForm from "./forms/RoleUpdateForm";

type RequestCreateContentProps = {
  onCreate: (createdRequest: RequestCreate) => Promise<void>;
  loading?: boolean;
};

const actionOptions = Object.entries(RequestAction).map(([key, value]) => ({
  label: key.charAt(0) + key.slice(1).toLowerCase(),
  key: value,
  value: value,
}));

const targetOptions = Object.entries(RequestTarget).map(([key, value]) => ({
  label: key.charAt(0) + key.slice(1).toLowerCase(),
  key: value,
  value: value,
}));

export default function RequestCreateContent({
  onCreate,
  loading,
}: RequestCreateContentProps) {
  const [action, setAction] = useState<RequestAction>(actionOptions[0].value);
  const [target, setTarget] = useState<RequestTarget>(targetOptions[0].value);
  const [justification, setJustification] = useState("");
  const [extraData, setExtraData] = useState("{}");
  const [files, setFiles] = useState<string[]>([]);

  function renderExtraDataForm() {
    switch (`${action}_${target}`) {
      case `${RequestAction.CREATE}_${RequestTarget.CLINIC}`:
        return (
          <ClinicCreateForm
            value={extraData ? JSON.parse(extraData) : {}}
            onChange={(v) => setExtraData(JSON.stringify(v))}
            disabled={loading}
          />
        );
      case `${RequestAction.CREATE}_${RequestTarget.CLINIC_MEMBERSHIP}`:
        return (
          <ClinicMembershipCreateForm
            value={extraData ? JSON.parse(extraData) : {}}
            onChange={(v) => setExtraData(JSON.stringify(v))}
            disabled={loading}
          />
        );
      case `${RequestAction.UPDATE}_${RequestTarget.ROLE}`:
        return (
          <RoleUpdateForm
            value={extraData ? JSON.parse(extraData) : {}}
            onChange={(v) => setExtraData(JSON.stringify(v))}
            disabled={loading}
          />
        );
      default:
        return (
          <CustomTextInput
            textLabel="Extra Data (JSON)"
            value={extraData}
            onChangeText={setExtraData}
            placeholder='{"key":"value"}'
            editable={!loading}
          />
        );
    }
  }

  const handleSubmit = async () => {
    try {
      const request: RequestCreate = {
        action,
        target,
        justification,
        extraData: JSON.parse(extraData),
        files,
      };
      await onCreate(request);
    } catch (error) {
      Alert.alert("Error", "Failed to create request");
    }
  };

  return (
    <View style={{ gap: 16 }}>
      <CustomList
        list={actionOptions}
        selectedItem={action}
        onSelect={setAction}
        disabled={loading}
        label={"Action"}
      />

      <CustomList
        list={targetOptions}
        selectedItem={target}
        onSelect={setTarget}
        disabled={loading}
        label={"Target"}
      />

      <CustomTextInput
        textLabel="Justification"
        value={justification}
        onChangeText={setJustification}
        placeholder="Justification"
        editable={!loading}
      />

      {renderExtraDataForm()}

      <CustomTextInput
        textLabel="Files (comma separated)"
        value={files.join(",")}
        onChangeText={(val) => setFiles(val.split(",").map((f) => f.trim()))}
        placeholder="file1.png,file2.pdf"
        editable={!loading}
      />

      <CustomButton
        onPress={handleSubmit}
        text="Create Request"
        disabled={loading}
      />
    </View>
  );
}
