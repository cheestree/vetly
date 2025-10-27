import { useState } from "react";

export function useForm<T extends Record<string, any>>(initial: T) {
  const [form, setForm] = useState<T>(initial);

  const handleInputChange = <K extends keyof T>(field: K, value: T[K]) => {
    setForm((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const getPatch = (): Partial<T> => {
    const patch: Partial<T> = {};
    for (const key in form) {
      if (form[key] !== initial[key]) {
        patch[key] = form[key];
      }
    }
    return patch;
  };

  return { form, setForm, handleInputChange, getPatch };
}
