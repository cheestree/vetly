import {
  actionOptions,
  RequestAction,
  RequestCreate,
  RequestTarget,
  targetOptions,
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

export default function RequestCreateContent({
  onCreate,
  loading,
}: RequestCreateContentProps) {
  const [form, setForm] = useState<{
    action: RequestAction;
    target: RequestTarget;
    justification: string;
    extraData: string;
    files: DocumentPicker.DocumentPickerAsset[];
  }>({
    action: actionOptions.filter((e) => e.value !== undefined)[0].value,
    target: targetOptions.filter((e) => e.value !== undefined)[0].value,
    justification: "",
    extraData: "{}",
    files: [],
  });

  const handlePickFiles = async () => {
    try {
      const result = await DocumentPicker.getDocumentAsync({
        type: "application/pdf",
        multiple: true,
        copyToCacheDirectory: true,
      });

      if (result.canceled === false) {
        setForm((prev) => ({
          ...prev,
          files: [...prev.files, ...(result.assets || [])],
        }));
      }
    } catch (error) {
      console.error("Failed to pick files:", error);
      Alert.alert("Error", "Failed to pick PDF file");
    }
  };

  const handleRemoveFile = (uri: string) => {
    setForm((prev) => ({
      ...prev,
      files: prev.files.filter((file) => file.uri !== uri),
    }));
  };

  const handleSubmit = async () => {
    try {
      const request: RequestCreate = {
        action: form.action,
        target: form.target,
        justification: form.justification,
        extraData: JSON.parse(form.extraData),
      };
      await onCreate(request, form.files);
    } catch (error) {
      console.error("Failed to create request:", error);
      Alert.alert("Error", "Failed to create request");
    }
  };

  function renderExtraDataForm() {
    switch (`${form.action}_${form.target}`) {
      case `${RequestAction.CREATE}_${RequestTarget.CLINIC}`:
        return (
          <ClinicCreateForm
            value={form.extraData ? JSON.parse(form.extraData) : {}}
            onChange={(v) =>
              setForm((prev) => ({
                ...prev,
                extraData: JSON.stringify(v),
              }))
            }
            disabled={loading}
          />
        );
      case `${RequestAction.CREATE}_${RequestTarget.CLINIC_MEMBERSHIP}`:
        return (
          <ClinicMembershipCreateForm
            value={form.extraData ? JSON.parse(form.extraData) : {}}
            onChange={(v) =>
              setForm((prev) => ({
                ...prev,
                extraData: JSON.stringify(v),
              }))
            }
            disabled={loading}
          />
        );
      case `${RequestAction.UPDATE}_${RequestTarget.ROLE}`:
        return (
          <RoleUpdateForm
            value={form.extraData ? JSON.parse(form.extraData) : {}}
            onChange={(v) =>
              setForm((prev) => ({
                ...prev,
                extraData: JSON.stringify(v),
              }))
            }
            disabled={loading}
          />
        );
      default:
        return (
          <CustomTextInput
            textLabel="Extra Data (JSON)"
            value={form.extraData}
            onChangeText={(text) =>
              setForm((prev) => ({ ...prev, extraData: text }))
            }
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
        selectedItem={form.action}
        onSelect={(value) => setForm((prev) => ({ ...prev, action: value }))}
        disabled={loading}
      />

      <CustomList
        list={targetOptions}
        selectedItem={form.target}
        onSelect={(value) => setForm((prev) => ({ ...prev, target: value }))}
        disabled={loading}
      />

      <CustomTextInput
        textLabel="Justification"
        value={form.justification}
        onChangeText={(text) =>
          setForm((prev) => ({ ...prev, justification: text }))
        }
        placeholder="Justification"
        editable={!loading}
      />

      {renderExtraDataForm()}

      <CustomButton
        onPress={handlePickFiles}
        text="Add PDF File(s)"
        disabled={loading}
      />

      {form.files.length > 0 && (
        <View>
          <CustomText text={`${form.files.length} file(s) selected`} />
          {form.files.map((file, idx) => (
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
