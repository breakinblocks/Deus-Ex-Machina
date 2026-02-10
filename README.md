# Deus Ex Machina

A NeoForge mod for Minecraft 1.21.1 that rewards persistence. Die to a mob, come back stronger.

## Overview

Deus Ex Machina introduces a buff system where dying to specific mobs grants you resistance against them and increased damage when fighting back. The more you die to a mob, the stronger you become against it.

## Getting Started

1. Craft or brew **Ambrosia** to gain the permanent **Deus Ex Machina** effect
2. Die to a configured mob
3. Respawn with resistance against that mob type
4. Kill the mob to reset your buffs (configurable)

## Features

- **Resistance Buffs**: Take reduced damage from mobs that have killed you
- **Attack Buffs**: Deal increased damage to mobs that have killed you
- **Death Screen Display**: See your buff gains when killed
- **Datapack Configuration**: Customize buffs per mob or entity tag
- **Instance Mode**: Track kills per individual mob instead of mob type

## Configuration

### Datapack System

Create JSON files in `data/<namespace>/deus_mobs/` to configure mob buffs.

**Example: `data/minecraft/deus_mobs/zombie.json`**
```json
{
  "target": "minecraft:zombie",
  "resistance": {
    "min": 0,
    "max": 80,
    "increase": 5,
    "reset": "FULL"
  },
  "attack": {
    "min": 0,
    "max": 50,
    "increase": 3,
    "reset": "PARTIAL"
  }
}
```

### Configuration Options

| Field | Description |
|-------|-------------|
| `target` | Entity ID (`minecraft:zombie`) or tag (`#minecraft:undead`) |
| `type` | `entity_type` (default) or `instance` |
| `min` | Minimum buff value |
| `max` | Maximum buff value |
| `increase` | Amount gained per death |
| `reset` | Reset behavior when killing the mob |

### Reset Behaviors

| Value | Description |
|-------|-------------|
| `NONE` | Buffs never reset |
| `FULL` | Reset to minimum on kill |
| `PARTIAL` | Reduce by `increase` amount on kill |

### Tracking Modes

| Mode | Description |
|------|-------------|
| `entity_type` | All mobs of a type share buff progression (default) |
| `instance` | Each individual mob tracks its own kill history |

**Instance mode example (Warden):**
```json
{
  "target": "minecraft:warden",
  "type": "instance",
  "resistance": { "min": 0, "max": 90, "increase": 3, "reset": "FULL" }
}
```

With instance mode, dying to Warden A only builds resistance against Warden A. Warden B is tracked separately.

## Default Configurations

The mod includes default configurations for:
- Warden (instance mode)
- Wither
- Ender Dragon

## Recipes

### Ambrosia
Brewed using the standard brewing system.

## Dependencies

- NeoForge for Minecraft 1.21.1
- Optional: JEI for recipe viewing

## License

See LICENSE file for details.
