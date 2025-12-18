#!/usr/bin/env node
import { Command } from "commander";
import { createRequire } from "module";
import { registerBadgeCommands } from "./commands/badge.js";
import { registerTeamRuleCommands } from "./commands/team-rule.js";

const program = new Command();
const require = createRequire(import.meta.url);
const { version } = require("../package.json") as { version: string };

program
    .name("cicd")
    .description(
        "CI/CD 파이프라인에서 사용하는 도구 모음입니다. GitLab 팀 규칙 검증, 배지 업데이트 등 다양한 자동화 작업을 수행할 수 있습니다.",
    )
    .version(version);

registerTeamRuleCommands(program);
registerBadgeCommands(program);

program.showHelpAfterError();

void program.parseAsync(process.argv);
