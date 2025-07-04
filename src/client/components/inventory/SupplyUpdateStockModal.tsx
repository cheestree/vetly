import { SupplyUpdate } from "@/api/supply/supply.input";
import size from "@/theme/size";
import React, { useState } from "react";
import { View } from "react-native";
import { Modal } from "react-native-paper";
import CustomButton from "../basic/custom/CustomButton";
import CustomText from "../basic/custom/CustomText";
import CustomTextInput from "../basic/custom/CustomTextInput";

type UpdateStockModalProps = {
  visible: boolean;
  currentStock?: number;
  currentPrice?: number;
  onDismiss: () => void;
  onSave: (update: SupplyUpdate) => void;
};

export default function UpdateStockModal({
  visible,
  currentStock,
  currentPrice,
  onDismiss,
  onSave,
}: UpdateStockModalProps) {
  const [form, setForm] = useState<SupplyUpdate>({
    quantity: currentStock ?? 0,
    price: currentPrice ?? 0,
  });
  const [saving, setSaving] = useState(false);

  const handleChange = <K extends keyof SupplyUpdate>(
    key: K,
    value: SupplyUpdate[K],
  ) => {
    setForm((prev) => ({ ...prev, [key]: value }));
  };

  const handleSave = async () => {
    setSaving(true);
    await onSave(form);
    setSaving(false);
  };

  return (
    <Modal
      visible={visible}
      onDismiss={onDismiss}
      style={{ justifyContent: "center", alignItems: "center" }}
    >
      <View
        style={{
          backgroundColor: "white",
          borderRadius: 12,
          padding: size.padding.lg,
          width: 320,
          alignItems: "center",
        }}
      >
        <CustomText
          text="Update Clinic Stock"
          style={{ fontWeight: "bold", fontSize: 18, marginBottom: 12 }}
        />
        <CustomTextInput
          textLabel="Stock"
          value={form.quantity?.toString() ?? ""}
          onChangeText={(text) =>
            handleChange("quantity", text === "" ? 0 : Number(text))
          }
          keyboardType="numeric"
          editable={!saving}
          style={{ marginBottom: 16, width: "100%" }}
        />
        <CustomTextInput
          textLabel="Price"
          value={form.price?.toString() ?? ""}
          onChangeText={(text) =>
            handleChange("price", text === "" ? 0 : Number(text))
          }
          keyboardType="numeric"
          editable={!saving}
          style={{ marginBottom: 16, width: "100%" }}
        />
        <View style={{ flexDirection: "row", gap: 12 }}>
          <CustomButton
            text="Cancel"
            onPress={onDismiss}
            variant="secondary"
            disabled={saving}
          />
          <CustomButton
            text={saving ? "Saving..." : "Save"}
            onPress={handleSave}
            disabled={
              saving ||
              isNaN(form.quantity ?? NaN) ||
              (form.quantity ?? 0) < 0 ||
              isNaN(form.price ?? NaN) ||
              (form.price ?? 0) < 0
            }
          />
        </View>
      </View>
    </Modal>
  );
}
