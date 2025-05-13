import React from "react";

interface WebDatePickerProps {
  selectedDate: string | null;
  onDateChange: (date: string | null) => void;
}

export default function WebDatePicker({
  selectedDate,
  onDateChange,
}: WebDatePickerProps) {
  const handleDateChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newDate = event.target.value;
    onDateChange(newDate);
  };

  return (
    <div>
      <input
        type="date"
        value={selectedDate?.toString() ?? ""}
        onChange={handleDateChange}
      />
      <button onClick={() => onDateChange(null)} disabled={!selectedDate}>
        Clear Date
      </button>
    </div>
  );
}
