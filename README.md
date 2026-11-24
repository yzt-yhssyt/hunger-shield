# Hunger Shield

> 把饥饿值变成 1000 点护盾：数字 HUD 双条，低血量时用饥饿值抵伤。
>
> Turns hunger and saturation into a 1000-point shield, with a numeric HUD and low-health damage absorption.

**Minecraft 1.21.1 · Fabric · 作者 yzt**

## 简介

饥饿条不再只是「能不能跑步」。它变成一条真正的资源：满的时候是 1000 点缓冲，生命掉到一半以下时，伤害先由它来付。

## 特性

- 饥饿值与饱和度上限提升到 1000，HUD 换成两条带数字的进度条（饱和度在上、饥饿在下）
- 生命 ≤50% 或这一击本来会致命时，伤害改由「饱和度 + 饥饿值」支付：1 点伤害 = 3 点饥饿值，期间完全不掉血
- 池子耗尽后按 1/3 比例结算剩余伤害，护甲、图腾与死亡判定照常工作，不会出现无敌
- 自然再生：饥饿 ≥500 快速再生、≥200 缓慢再生，与原版节奏一致
- 饥饿消耗与疲劳 ×10，疲劳上限 400，鼓励认真种田
- 旧存档自动迁移（饥饿与饱和度 ×50），只迁移一次，不会重复放大
- 独立运行，无需 Fabric API；也是「营养」的前置

## 安装

1. 安装 Minecraft 1.21.1 与 Fabric Loader（≥ 0.16.14）
2. 安装依赖：Fabric Loader（必需，无需 Fabric API）
3. 把 `hunger-shield-1.0.0.jar` 放进 `mods` 文件夹

## 依赖

Fabric Loader（必需，无需 Fabric API）

## English

The hunger bar stops being just a sprint meter. It becomes a real resource: 1000 points of buffer when full, and the first thing that pays when your health drops below half.

- Hunger and saturation now cap at 1000, with a two-bar numeric HUD (saturation on top, hunger below)
- At or below 50% health, or when a hit would be lethal, damage is paid from saturation + hunger at 3 points per damage, and health is untouched
- When the pool runs dry the remainder is applied at one third rate, so armor, totems and death still work — no invulnerability
- Natural regeneration: fast above 500 hunger, slow above 200, matching vanilla pacing
- Exhaustion and hunger drain are ×10 with a 400 exhaustion cap, so farming still matters
- Existing worlds migrate automatically (hunger and saturation ×50) exactly once
- Standalone, no Fabric API needed; also the required base for Nutrition

### Installation

1. Minecraft 1.21.1 with Fabric Loader (≥ 0.16.14)
2. Dependencies: Fabric Loader (required, no Fabric API needed)
3. Drop `hunger-shield-1.0.0.jar` into your `mods` folder

## 链接

- 源码 / Source: https://github.com/yzt-yhssyt/hunger-shield
- 问题反馈 / Issues: https://github.com/yzt-yhssyt/hunger-shield/issues

## 许可

MIT License © 2026 yzt. 你可以自由使用、修改、再分发，包括放进整合包。
