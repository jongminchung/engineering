import pc from "picocolors";

export const color = {
  info: (message: string) => pc.cyan(message),
  success: (message: string) => pc.green(pc.bold(message)),
  warn: (message: string) => pc.yellow(message),
  error: (message: string) => pc.red(pc.bold(message)),
  banner: (message: string) => pc.bold(pc.magenta(message)),
  label: (message: string) => pc.bold(message),
  muted: (message: string) => pc.gray(message),
};

export function formatEnvStatus(value?: string): string {
  return value ? color.success("<설정됨>") : color.warn("<설정되지 않음>");
}
