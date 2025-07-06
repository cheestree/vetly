import { z } from "zod";

export const GuideCreateSchema = z.object({
  title: z.string().trim().min(1, "Title is required"),
  description: z.string().trim().min(1, "Description is required"),
  content: z.string().trim().min(1, "Content is required"),
});

export const GuideUpdateSchema = z.object({
  title: z.string().trim().min(1, "Title is required"),
  description: z.string().trim().optional(),
  content: z.string().trim().optional(),
});

export const GuideQuerychema = z.object({
  title: z.string().trim().min(1, "Title is required").optional(),
});

export type GuideCreateInput = z.infer<typeof GuideCreateSchema>;
export type GuideUpdateInput = z.infer<typeof GuideUpdateSchema>;
export type GuideQueryInput = z.infer<typeof GuideQuerychema>;
