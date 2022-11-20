# Attack Modifier Basics

### This plugin is basically a cosmetic-only simple implementation of how this could work.
 
 You may get hit by your own attacks.

## Installation
**Prebuilt JAR:** 
- Get latest AttackModifier.jar release from [releases](https://github.com/NotThorny/AttackModifier/releases) and place it in your `\grasscutter\plugins` folder.

**Build from source:**
- Run these commands:
```
cd AttackModifier 
mvn clean install
```
 
 Restart the server if it was already running.
 
 ## Usage
 
 `/at remove` to clear all gadgets

 `/at reload` to reload the config

 `/at set n|e|q [gadgetId]` to set the gadget for the current character's normal attack (n), elemental skill (e), or burst (q)
 
 `/at off|on` to toggle off/on the effects. Effects are enabled by default.
 
 ## Modifying
 
 Now with `config.json` yay
 
 ```json
 "ayakaIds": {
    "skill": {
      "normalAtk": 0,
      "elementalSkill": 0,
      "elementalBurst": 0
    }
  }
 ```
