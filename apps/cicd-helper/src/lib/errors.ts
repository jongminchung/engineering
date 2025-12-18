import { color } from "./colors.js";

export function handleActionError(error: unknown): never {
  const message = error instanceof Error ? error.message : String(error);
  console.error(color.error(message));
  process.exit(1);
}
