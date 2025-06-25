import { SupplyAssociate } from "@/api/supply/supply.input";
import { useState } from "react";
import { View } from "react-native";
import { Modal } from "react-native-paper";
import CustomButton from "../basic/custom/CustomButton";
import CustomList from "../basic/custom/CustomList";
import CustomTextInput from "../basic/custom/CustomTextInput";

type Props = {
  visible: boolean;
  onDismiss: () => void;
  clinics: { id: number; name: string }[];
  supplies: { id: number; name: string }[];
  onAssociate: (clinicId: number, input: SupplyAssociate) => void;
};

export default function AddSupplyToClinicModal({
  visible,
  onDismiss,
  clinics,
  supplies,
  onAssociate,
}: Props) {
  const [selectedClinic, setSelectedClinic] = useState<number | undefined>();
  const [selectedSupply, setSelectedSupply] = useState<number | undefined>();
  const [quantity, setQuantity] = useState<string>("");
  const [price, setPrice] = useState<string>("");

  const clinicOptions = clinics.map((c) => ({
    label: c.name,
    key: c.id,
    value: c.id,
  }));
  const supplyOptions = supplies.map((s) => ({
    label: s.name,
    key: s.id,
    value: s.id,
  }));

  const handleQuantityChange = (val: string) => {
    if (/^\d*$/.test(val)) setQuantity(val);
  };

  const handlePriceChange = (val: string) => {
    if (/^\d*\.?\d*$/.test(val)) setPrice(val);
  };

  return (
    <Modal
      visible={visible}
      onDismiss={onDismiss}
      contentContainerStyle={{
        padding: 16,
        backgroundColor: "#fff",
        borderRadius: 8,
      }}
    >
      <View style={{ gap: 16 }}>
        <CustomList
          label="Clinic"
          list={clinicOptions}
          selectedItem={selectedClinic}
          onSelect={setSelectedClinic}
        />

        <CustomList
          label="Supply"
          list={supplyOptions}
          selectedItem={selectedSupply}
          onSelect={setSelectedSupply}
        />

        <CustomTextInput
          textLabel="Quantity"
          value={quantity}
          onChangeText={handleQuantityChange}
          keyboardType="numeric"
        />

        <CustomTextInput
          textLabel="Price"
          value={price}
          onChangeText={handlePriceChange}
          keyboardType="numeric"
        />

        <CustomButton
          text="Associate"
          onPress={() => {
            if (selectedClinic && selectedSupply) {
              onAssociate(selectedClinic, {
                supplyId: selectedSupply,
                quantity: Number(quantity),
                price: Number(price),
              });
            }
          }}
          disabled={
            !selectedClinic ||
            !selectedSupply ||
            !quantity ||
            !price ||
            isNaN(Number(quantity)) ||
            isNaN(Number(price))
          }
        />

        <CustomButton text="Cancel" onPress={onDismiss} />
      </View>
    </Modal>
  );
}
