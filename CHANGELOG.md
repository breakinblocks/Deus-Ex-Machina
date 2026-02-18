# Changelog

All notable changes to Deus Ex Machina will be documented in this file.

## [1.2.0] - 2026-02-18

### Added
- **Admin Commands** — `/deus_ex_machina set|add|remove|reset` commands for managing buff values per entity type per player (requires OP level 2)
- `/deus_ex_machina reset all <entityType>` to reset all buffs for a specific entity type
- `/deus_ex_machina reset all all` to reset all buffs for all entities
- Tab-completion for buff types and entity types in all commands
- CurseForge publishing support in build configuration

### Fixed
- **Regex mob keys now store per-entity** — Regex targets (e.g. `/minecraft:.*/`) now correctly store buff data under each entity's own ID instead of grouping everything under the regex pattern string. Regex patterns act as eligibility matchers only; each matching entity type gets its own independent buff progression.

### Changed
- Changed Ambrosia Recipe to use a Strong Healing Potion instead of a Strong Regen Potion
- Refactored Brewing Registry to support multiple recipes with JEI integration
- Split `getGroupKey` (storage key) from `getConfigKey` (config lookup key) in mob handler to properly separate buff data storage from datapack configuration resolution

## [1.1] - 2025-02-12

### Added
- **Extensible Buff Type API** — New registry-based system allowing arbitrary buff types to be registered
- **11 Buff Types**: Resistance, Attack Boost, Evasion, Thorns, Life Leech, Critical Chance, Execute, Knockback, Knockback Resistance, Berserker, Momentum
- **Buff Categories**: Resistance, Damage, Movement, Health, Misc — each category determines when a buff applies (on hurt, on attack, or both)
- **Instance Mode Tracking** — Mobs can now track buffs per-instance (buffs are stored on the mob and die with it) in addition to the existing entity_type mode (buffs stored on the player)
- Per-mob-instance data capability for tracking which players have gained buffs from a specific mob
- `TypeEnum` to configure `entity_type` vs `instance` tracking per mob in datapacks

### Changed
- Datapack mob configs now use a `"buffs"` map keyed by registry IDs (e.g. `"deus_ex_machina:resistance"`) with per-buff `min`, `max`, `increase`, and `reset` settings
- Data layer refactored from hardcoded resistance/strength hashmaps to generic `Map<String, Map<ResourceLocation, Integer>>` buff storage
- Death screen dynamically renders all active buff types with their registered display names and colors
- Network packets now transmit a dynamic map of buff changes instead of fixed resistance/strength fields
- Event handlers iterate over the buff registry instead of handling resistance and strength separately
- Updated Ambrosia item texture

## [1.0.3] - 2025-01-30

### Added
- **Datapack-driven mob configuration** — Mob buff settings are now loaded from JSON files in `data/<namespace>/deus_mobs/` instead of TOML config
- **Entity tag support** — Mob configs can target entity tags (e.g. `#minecraft:skeletons`) in addition to individual entity types
- Selective buff display on death screen based on datapack configuration (only shows buffs the player actually gained)

### Fixed
- Don't apply buffs in hardcore mode since the player will be deleted on death anyway

## [1.0.2] - 2025-01-25

### Changed
- Refactored configuration to use enums for DeusMode and Reset options

### Fixed
- Disabled curing of Deus Ex Machina potion effect (milk, totems, etc. no longer remove it)

## [1.0.1] - 2025-01-20

### Added
- **JEI integration** — Ambrosia item now shows in Just Enough Items
- Improved mod versioning and archive naming convention

## [1.0] - 2025-01-15

### Added
- Core mod functionality — players gain stacking resistance and strength buffs when killed by configured boss mobs
- Death screen overlay showing buff gains per death
- Ambrosia consumable item to reset all accumulated buffs
- Deus Ex Machina potion effect
- Configurable buff values (min, max, increase, reset) per mob via TOML config
- Default boss configurations for Ender Dragon, Wither, and Warden
