import { useState } from "react";

export function useForm<T>(initial: T) {
  const [form, setForm] = useState<T>(initial);

  const handleInputChange = <K extends keyof T>(field: K, value: T[K]) => {
    setForm((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  return { form, setForm, handleInputChange };
}
