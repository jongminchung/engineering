import type { Command } from "commander";
import { color } from "../lib/colors.js";
import { handleActionError } from "../lib/errors.js";
import {
    createGitlabClient,
    DEFAULT_GITLAB_URL,
    normalizeBaseUrl,
    parseNumericId,
} from "../lib/gitlab.js";

type MergeRequestForValidation = {
    squash?: boolean;
    targetBranch?: string;
    target_branch?: string;
};

interface MrSquashOptions {
    projectId?: string;
    mergeRequestIid?: string;
    token?: string;
    baseUrl?: string;
}

export function registerTeamRuleCommands(program: Command): void {
    const teamRuleCommand = program
        .command("team-rule")
        .description("팀 규칙 관련 도구");

    teamRuleCommand
        .command("mr-squash")
        .description("Merge Request Squash 설정 검증")
        .option(
            "--project-id <id>",
            "GitLab 프로젝트 ID",
            process.env.CI_PROJECT_ID,
        )
        .option(
            "--merge-request-iid <iid>",
            "GitLab Merge Request IID",
            process.env.CI_MERGE_REQUEST_IID,
        )
        .option(
            "--token <token>",
            "GitLab Private Token",
            process.env.GITLAB_TOKEN,
        )
        .option(
            "--base-url <url>",
            "GitLab Base URL",
            process.env.GITLAB_BASE_URL ?? DEFAULT_GITLAB_URL,
        )
        .action(async (options: MrSquashOptions) => {
            try {
                await validateMergeRequestSquash(options);
                console.log(
                    color.success(
                        "✅ 규칙 1. Merge Request Squash 설정 검증 통과",
                    ),
                );
            } catch (error) {
                handleActionError(error);
            }
        });
}

async function validateMergeRequestSquash({
    projectId,
    mergeRequestIid,
    token,
    baseUrl,
}: MrSquashOptions): Promise<void> {
    if (!mergeRequestIid) {
        console.log(color.warn("🚀 CI_MERGE_REQUEST_IID가 없기에 스킵한다."));
        return;
    }

    if (!projectId) {
        throw new Error("🚨 PROJECT_ID 환경 변수를 설정하세요.");
    }

    if (!token) {
        throw new Error("🚨 GITLAB_TOKEN 환경 변수를 설정하세요.");
    }

    const trimmedBaseUrl = normalizeBaseUrl(baseUrl);
    const projectNumericId = parseNumericId(projectId, "CI_PROJECT_ID");
    const mrNumericId = parseNumericId(mergeRequestIid, "CI_MERGE_REQUEST_IID");

    const client = createGitlabClient(trimmedBaseUrl, token);
    console.log(color.info("🔍 Merge Request 정보를 불러오는 중입니다!"));

    let mergeRequest: MergeRequestForValidation;
    try {
        mergeRequest = (await client.MergeRequests.show(
            projectNumericId,
            mrNumericId,
        )) as MergeRequestForValidation;
    } catch (error) {
        console.error(color.error(JSON.stringify(error)));
        const status = extractStatusCode(error);
        if (status === 404) {
            throw new Error(
                "🚨 Merge Request 정보를 찾을 수 없습니다. 프로젝트 ID, MR IID, 토큰 권한을 확인하세요.",
            );
        }
        throw error;
    }

    const squash = mergeRequest.squash;
    const targetBranch =
        mergeRequest.targetBranch ?? mergeRequest.target_branch ?? "알 수 없음";

    console.log(
        `${color.info("📌 대상 브랜치:")} ${color.label(
            targetBranch,
        )} ${color.info("/ squash: ")}${
            squash ? color.success("ON") : color.warn("OFF")
        }`,
    );

    if (targetBranch === "master" && squash) {
        throw new Error(
            "🚨 master으로 병합은 squash merge를 허용하지 않습니다.",
        );
    }

    if (targetBranch === "develop" && !squash) {
        throw new Error("🚨 develop으로 병합은 squash merge만 허용합니다.");
    }
}

function extractStatusCode(error: unknown): number | undefined {
    if (!error || typeof error !== "object") {
        return undefined;
    }

    const status = (error as { status?: unknown }).status;
    if (typeof status === "number") {
        return status;
    }

    const responseStatus = (error as { response?: { status?: unknown } })
        .response?.status;
    if (typeof responseStatus === "number") {
        return responseStatus;
    }

    const responseStatusCode = (
        error as { response?: { statusCode?: unknown } }
    ).response?.statusCode;
    if (typeof responseStatusCode === "number") {
        return responseStatusCode;
    }

    const causeDescription = (error as { cause?: { description?: unknown } })
        .cause?.description;
    if (typeof causeDescription === "string") {
        const match = causeDescription.match(/\b(4\d\d|5\d\d)\b/);
        if (match) {
            return Number(match[1]);
        }
    }

    const message = (error as { message?: unknown }).message;
    if (typeof message === "string") {
        const match = message.match(/\b(4\d\d|5\d\d)\b/);
        if (match) {
            return Number(match[1]);
        }
    }

    return undefined;
}
