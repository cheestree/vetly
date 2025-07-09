import { z } from "zod";

export const UserUpdateSchema = z.object({
  username: z.string().trim().min(1, "Username is required").optional(),
});

export type UserUpdateInput = z.infer<typeof UserUpdateSchema>;
