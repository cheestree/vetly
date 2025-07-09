import { ServiceType } from "@/api/clinic/clinic.output";
import { z } from "zod";

const ServiceTypeEnum = z.nativeEnum(ServiceType);

const OpeningHourModel = z.object({
  weekDay: z.number(),
  opensAt: z.string(),
  closesAt: z.string(),
});

export const ClinicCreateSchema = z.object({
  name: z.string().min(1, "Clinic name is required"),
  nif: z.string().min(1, "Clinic NIF is required"),
  address: z.string().min(1, "Clinic address is required"),
  lng: z.number(),
  lat: z.number(),
  phone: z.string().min(1),
  email: z.string().email(),
  services: z.array(ServiceTypeEnum).optional(),
  openingHours: z.array(OpeningHourModel).optional(),
  ownerEmail: z.string().email().optional(),
});
