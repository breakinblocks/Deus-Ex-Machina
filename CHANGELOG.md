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

---

## [1.1.0]

### Added
- **Extensible Buff Type API**: New registry-based system replacing hardcoded resistance/strength buffs. Third-party mods can register custom buff types.
- **Buff Registry**: All buff types are registered with a `ResourceLocation` ID, display name, color, and default settings.
- **11 Built-in Buff Types**:
  - **Resistance** — Reduces incoming damage by a percentage
  - **Attack Boost** — Increases outgoing damage by a percentage
  - **Evasion** — Chance to completely dodge an attack
  - **Thorns** — Reflects a percentage of incoming damage back to the attacker
  - **Life Leech** — Heals the player for a percentage of damage dealt
  - **Critical Chance** — Chance to deal 1.5x damage on hit
  - **Execute** — Bonus damage against targets below 30% health
  - **Knockback** — Pushes targets further on hit
  - **Knockback Resistance** — Reduces knockback taken by the player
  - **Berserker** — Take more damage but deal even more (high risk/reward, applies on both hurt and attack)
  - **Momentum** — Stacking damage bonus on consecutive hits, resets when hit
- **Buff Categories**: `RESISTANCE`, `DAMAGE`, `MOVEMENT`, `HEALTH`, `MISC` — determines when a buff applies (on hurt, on attack, or both)
- **Datapack-Driven Mob Configuration**: Mob buff settings are now configured via datapacks instead of TOML config. Create JSON files in `data/<namespace>/deus_mobs/` to customize any mob.
- **Entity Tag Support**: Configure buffs for entire groups of mobs using entity tags (e.g., `#minecraft:undead`). Tag configs apply to all matching entities.
- **Instance Mode Tracking**: New `"type"` field in datapack JSON with two modes:
  - `entity_type` (default): All mobs of a type share buff progression
  - `instance`: Each individual mob tracks its own kill history
- **Selective Buff Display**: Death screen only shows buffs that are enabled for that mob in the datapack.

### Changed
- Datapack JSON now uses a `"buffs"` map keyed by registry IDs instead of separate `"resistance"` and `"attack"` objects.
- Death screen dynamically renders all active buff types with their registered display names and colors.
- Network packets transmit a dynamic map of buff changes instead of fixed resistance/strength fields.
- Warden now uses instance mode - dying to one warden only builds resistance against that specific warden.
- Reset behavior now uses enums (`NONE`, `FULL`, `PARTIAL`) instead of strings.
- Mob data for instance mode stored on entity attachment - automatically cleaned up on mob death.
- Updated Ambrosia item texture.

### Example Datapack JSON
```json
{
  "target": "minecraft:warden",
  "type": "instance",
  "buffs": {
    "deus_ex_machina:resistance": { "min": 0, "max": 80, "increase": 2, "reset": "FULL" },
    "deus_ex_machina:attack_boost": { "min": 0, "max": 80, "increase": 2, "reset": "FULL" },
    "deus_ex_machina:evasion": { "min": 0, "max": 30, "increase": 1, "reset": "FULL" }
  }
}
```

---

## [1.0.2]

### Fixed
- Deus Ex Machina effect can no longer be cured by milk or other curing methods.

---

## [1.0.1]

### Added
- JEI (Just Enough Items) integration for the Ambrosia item.

---

## [1.0.0]

### Added
- Initial release for NeoForge 1.21.1
- **Deus Ex Machina Effect**: Permanent effect that enables the buff system.
- **Ambrosia Item**: Consumable that grants the Deus Ex Machina effect.
- **Resistance Buffs**: Gain damage resistance against mobs that kill you.
- **Attack Buffs**: Deal increased damage to mobs that have killed you.
- **Death Screen Display**: Shows buff gains when killed by a configured mob.
- **Configurable Mob List**: Define which mobs participate in the buff system.
- **Configurable Buff Settings**: Adjust min/max values, increase amounts, and reset behavior.
- **Brewing Recipe**: Brew Ambrosia using vanilla brewing mechanics.
