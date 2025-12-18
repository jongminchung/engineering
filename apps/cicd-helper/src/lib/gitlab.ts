import { Gitlab } from "@gitbeaker/rest";

export const DEFAULT_GITLAB_URL = "https://gitlab.gabia.com";

export function normalizeBaseUrl(baseUrl?: string): string {
    if (!baseUrl) {
        return DEFAULT_GITLAB_URL;
    }
    return baseUrl.endsWith("/") ? baseUrl.slice(0, -1) : baseUrl;
}

export function parseNumericId(value: string, label: string): number {
    const parsed = Number(value);
    if (!Number.isFinite(parsed)) {
        throw new Error(`🚨 ${label} 값이 유효한 숫자가 아닙니다.`);
    }
    return parsed;
}

export function createGitlabClient(host: string, token: string): Gitlab {
    return new Gitlab({ host, token });
}
