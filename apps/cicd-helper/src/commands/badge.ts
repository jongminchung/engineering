import type { Command } from "commander";
import { color, formatEnvStatus } from "../lib/colors.js";
import { handleActionError } from "../lib/errors.js";
import {
    createGitlabClient,
    DEFAULT_GITLAB_URL,
    normalizeBaseUrl,
    parseNumericId,
} from "../lib/gitlab.js";

interface BadgeOptions {
    projectId?: string;
    badgeId?: string;
    badgeUrl?: string;
    badgeImage?: string;
    token?: string;
    baseUrl?: string;
}

export function registerBadgeCommands(program: Command): void {
    const badgeCommand = program
        .command("badge")
        .description("GitLab 배지 관리 도구");

    badgeCommand
        .command("upload")
        .description("common/badge.gitlab-ci.yml 과 동일한 배지 업데이트 실행")
        .option(
            "--project-id <id>",
            "GitLab 프로젝트 ID",
            process.env.CI_PROJECT_ID,
        )
        .option("--badge-id <id>", "업데이트할 배지 ID", process.env.BADGE_ID)
        .option("--badge-url <url>", "배지 링크 URL", process.env.BADGE_URL)
        .option(
            "--badge-image <url>",
            "배지 이미지 URL",
            process.env.BADGE_IMAGE,
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
        .action(async (options: BadgeOptions) => {
            try {
                await updateProjectBadge(options);
                console.log(color.success("🏷️ GitLab 배지 업데이트 완료"));
            } catch (error) {
                handleActionError(error);
            }
        });
}

async function updateProjectBadge({
    projectId,
    badgeId,
    badgeUrl,
    badgeImage,
    token,
    baseUrl,
}: BadgeOptions): Promise<void> {
    logBadgeChecklist({
        token,
        badgeId,
        badgeUrl,
        badgeImage,
    });

    if (!projectId) {
        throw new Error("🚨 CI_PROJECT_ID 환경 변수를 설정하세요.");
    }
    if (!token) {
        throw new Error("🚨 GITLAB_TOKEN 환경 변수를 설정하세요.");
    }
    if (!badgeId || !badgeUrl || !badgeImage) {
        throw new Error(
            "🚨 BADGE_ID, BADGE_URL, BADGE_IMAGE 환경 변수를 모두 설정하세요.",
        );
    }

    const trimmedBaseUrl = normalizeBaseUrl(baseUrl);
    const client = createGitlabClient(trimmedBaseUrl, token);
    const projectNumericId = parseNumericId(projectId, "CI_PROJECT_ID");
    const badgeNumericId = parseNumericId(badgeId, "BADGE_ID");

    console.log(color.info("🔄 GitLab 배지를 업데이트하는 중입니다..."));

    await client.ProjectBadges.edit(projectNumericId, badgeNumericId, {
        linkUrl: badgeUrl,
        imageUrl: badgeImage,
    });
}

function logBadgeChecklist({
    token,
    badgeId,
    badgeUrl,
    badgeImage,
}: {
    token?: string;
    badgeId?: string;
    badgeUrl?: string;
    badgeImage?: string;
}): void {
    const banner = [
        "╔═════════════════════════════════════════════════════════════╗",
        "║                배지 설정을 위한 필수 변수 확인                     ║",
        "╚═════════════════════════════════════════════════════════════╝",
    ].join("\n");
    console.log(color.banner(banner));
    console.log(
        color.info(
            "\n  배지 설정을 위해 다음의 필수 변수들이 올바르게 설정되었는지 확인합니다.\n",
        ),
    );
    console.log(
        `  - ${color.label("GITLAB_TOKEN")}: ${formatEnvStatus(token)}`,
    );
    console.log(
        `  - ${color.label("BADGE_ID")}:     ${formatEnvStatus(badgeId)}`,
    );
    console.log(
        `  - ${color.label("BADGE_URL")}:    ${formatEnvStatus(badgeUrl)}`,
    );
    console.log(
        `  - ${color.label("BADGE_IMAGE")}:  ${formatEnvStatus(badgeImage)}`,
    );
    console.log(color.muted("\n  값이 누락되면 실행이 중단됩니다.\n"));
}
