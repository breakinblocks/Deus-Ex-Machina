# Changelog

All notable changes to Deus Ex Machina will be documented in this file.

## [1.1.0]

### Added
- **Datapack-Driven Mob Configuration**: Mob buff settings are now configured via datapacks instead of TOML config. Create JSON files in `data/<namespace>/deus_mobs/` to customize any mob.
- **Entity Tag Support**: Configure buffs for entire groups of mobs using entity tags (e.g., `#minecraft:undead`). Tag configs apply to all matching entities.
- **Instance Mode Tracking**: New `"type"` field in datapack JSON with two modes:
  - `entity_type` (default): All mobs of a type share buff progression
  - `instance`: Each individual mob tracks its own kill history
- **Selective Buff Display**: Death screen only shows buffs that are enabled for that mob in the datapack.

### Changed
- Warden now uses instance mode - dying to one warden only builds resistance against that specific warden.
- Reset behavior now uses enums (`NONE`, `FULL`, `PARTIAL`) instead of strings.
- Mob data for instance mode stored on entity attachment - automatically cleaned up on mob death.

### Example Datapack JSON
```json
{
  "target": "minecraft:warden",
  "type": "instance",
  "resistance": { "min": 0, "max": 90, "increase": 3, "reset": "FULL" },
  "attack": { "min": 0, "max": 50, "increase": 2, "reset": "FULL" }
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
