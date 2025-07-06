import { z } from "zod";

export const CheckupCreateSchema = z.object({
  animalId: z.number().min(1, "Animal ID is required"),
  veterinarianId: z.string().uuid("Veterinarian ID is required"),
  clinicId: z.number().min(1, "Clinic ID is required"),
  dateTime: z
    .string()
    .trim()
    .regex(
      /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/,
      "Date and time must be in ISO format: YYYY-MM-DDTHH:MM:SS",
    ),
  title: z.string().trim().min(1, "Title is required"),
  description: z.string().trim().min(1, "Description is required"),
});

export const CheckupUpdateSchema = z.object({
  title: z.string().trim().min(1, "Title is required").optional(),
  dateTime: z
    .string()
    .trim()
    .regex(
      /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/,
      "Date and time must be in ISO format: YYYY-MM-DDTHH:MM:SS",
    )
    .optional()
    .transform((val) => (val === "" ? undefined : val)),
  description: z.string().trim().min(1, "Description is required").optional(),
});

export const CheckupQuerychema = z.object({});

export type CheckupCreateInput = z.infer<typeof CheckupCreateSchema>;
export type CheckupUpdateInput = z.infer<typeof CheckupUpdateSchema>;
export type CheckupQueryInput = z.infer<typeof CheckupQuerychema>;
