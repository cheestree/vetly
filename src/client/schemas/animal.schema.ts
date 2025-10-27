import { Sex } from "@/api/animal/animal.input";
import { z } from "zod";

export const AnimalCreateSchema = z.object({
  name: z.string().trim().min(1, "Name is required"),
  microchip: z
    .string()
    .trim()
    .refine((val) => val.length === 0 || val.length >= 1, {
      message: "Microchip is required",
    })
    .optional()
    .transform((val) => (val === "" ? undefined : val)),
  sex: z.nativeEnum(Sex, {
    errorMap: () => ({ message: "Sex is required and must be valid" }),
  }),
  sterilized: z.boolean(),
  species: z
    .union([z.string().trim().min(1, "Species is required"), z.literal("")])
    .optional()
    .transform((val) => (val === "" ? undefined : val)),
  birthDate: z
    .string()
    .trim()
    .regex(
      /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/,
      "Birthdate must be in ISO format: YYYY-MM-DDTHH:MM:SS",
    )
    .optional()
    .transform((val) => (val === "" ? undefined : val)),
  ownerEmail: z
    .union([
      z.string().trim().email("Owner's email must be valid"),
      z.literal(""),
    ])
    .optional()
    .transform((val) => (val === "" ? undefined : val)),
});

export const AnimalUpdateSchema = z.object({
  name: z.string().trim().min(1, "Name is required").optional(),
  microchip: z
    .string()
    .trim()
    .min(1, "Description is required")
    .optional()
    .nullable(),
  sex: z
    .nativeEnum(Sex, {
      errorMap: () => ({ message: "Sex is required and must be valid" }),
    })
    .optional(),
  sterilized: z.boolean().optional(),
  species: z
    .string()
    .trim()
    .min(1, "Species is required")
    .optional()
    .nullable(),
  birthDate: z
    .string()
    .trim()
    .regex(
      /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/,
      "Birthdate must be in ISO format: YYYY-MM-DDTHH:MM:SS",
    )
    .optional()
    .nullable(),
  ownerEmail: z
    .string()
    .trim()
    .email("Owner's email must be valid")
    .optional()
    .nullable(),
});

export const AnimalQuerySchema = z.object({
  userEmail: z.string().trim().email("Owner's email must be valid").optional(),
  name: z.string().trim().min(1, "Name is required").optional(),
  microchip: z.string().trim().min(1, "Microchip is required").optional(),
  birthDate: z
    .string()
    .trim()
    .regex(/^\d{4}-\d{2}-\d{2}$/, "Birthdate must be in YYYY-MM-DD format")
    .optional(),
  species: z.string().trim().min(1, "Species is required").optional(),
  owned: z.boolean().optional(),
  self: z.boolean().nullable().optional(),
  active: z.boolean().nullable().optional(),
});

export type AnimalCreateInput = z.infer<typeof AnimalCreateSchema>;
export type AnimalUpdateInput = z.infer<typeof AnimalUpdateSchema>;
export type AnimalQueryInput = z.infer<typeof AnimalQuerySchema>;
