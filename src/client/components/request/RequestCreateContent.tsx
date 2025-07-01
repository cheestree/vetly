import {
  RequestAction,
  RequestCreate,
  RequestTarget,
} from "@/api/request/request.input";
import * as DocumentPicker from "expo-document-picker";
import { useState } from "react";
import { Alert, View } from "react-native";
import CustomButton from "../basic/custom/CustomButton";
import CustomList from "../basic/custom/CustomList";
import CustomText from "../basic/custom/CustomText";
import CustomTextInput from "../basic/custom/CustomTextInput";
import ClinicCreateForm from "./forms/ClinicCreateForm";
import ClinicMembershipCreateForm from "./forms/ClinicMembershipCreateForm";
import RoleUpdateForm from "./forms/RoleUpdateForm";

type RequestCreateContentProps = {
  onCreate: (
    createdRequest: RequestCreate,
    files?: DocumentPicker.DocumentPickerAsset[],
  ) => Promise<void>;
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
  const [files, setFiles] = useState<DocumentPicker.DocumentPickerAsset[]>([]);

  const handlePickFiles = async () => {
    try {
      const result = await DocumentPicker.getDocumentAsync({
        type: "application/pdf",
        multiple: true,
        copyToCacheDirectory: true,
      });

      if (result.canceled === false) {
        // result.assets is an array of selected files (for multiple)
        setFiles((prev) => [...prev, ...(result.assets || [])]);
      }
    } catch (error) {
      console.error("Failed to pick files:", error);
      Alert.alert("Error", "Failed to pick PDF file");
    }
  };

  const handleRemoveFile = (uri: string) => {
    setFiles((prev) => prev.filter((file) => file.uri !== uri));
  };

  const handleSubmit = async () => {
    try {
      const request: RequestCreate = {
        action,
        target,
        justification,
        extraData: JSON.parse(extraData),
      };
      await onCreate(request, files);
    } catch (error) {
      console.error("Failed to create request:", error);
      Alert.alert("Error", "Failed to create request");
    }
  };

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

      <CustomButton
        onPress={handlePickFiles}
        text="Add PDF File(s)"
        disabled={loading}
      />

      {files.length > 0 && (
        <View>
          <CustomText text={`${files.length} file(s) selected`} />
          {files.map((file, idx) => (
            <View
              key={file.uri || idx}
              style={{
                flexDirection: "row",
                alignItems: "center",
                marginVertical: 4,
              }}
            >
              <CustomTextInput
                value={file.name || "PDF"}
                editable={false}
                style={{ flex: 1 }}
              />
              <CustomButton
                text="Remove"
                onPress={() => handleRemoveFile(file.uri)}
                style={{ marginLeft: 8 }}
              />
            </View>
          ))}
        </View>
      )}

      <CustomButton
        onPress={handleSubmit}
        text="Create Request"
        disabled={loading}
      />
    </View>
  );
}
