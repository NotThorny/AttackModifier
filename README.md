# Attack Modifier Basics

### This plugin is basically a cosmetic-only simple implementation of how this could work.
 
 You may get hit by your own attacks.
 
 Preview:
 https://github.com/NotThorny/AttackModifier/assets/107363768/e6e5c53d-f692-4f5a-976f-9ef2bfd98b6e

## Installation
**Prebuilt JAR (Recommended):** 
- Get latest AttackModifier.jar release from [releases](https://github.com/NotThorny/AttackModifier/releases) and place it in your `\grasscutter\plugins` folder.

**Build from source:**
- Run these commands:
```
cd AttackModifier 
mvn clean install
```
- Place built jar into `\grasscutter\plugins\` folder.
 
 Restart the server if it was already running.
 
 ## Usage
 
 `/at remove` to clear all gadgets

 `/at reload` to reload the config

 `/at set n|e|q [gadgetId]` to set the gadget for the current character's normal attack (n), elemental skill (e), or burst (q)
 
 `/at off|on` to toggle off/on the effects. Effects are enabled by default.
 
 Gadget ids can be found from a list such as [Jie's GrasscutterCommandGenerator](https://github.com/jie65535/GrasscutterCommandGenerator/blob/main/Source/GrasscutterTools/Resources/en-us/Gadget.txt), or in your Grasscutter resources folder: GadgetExcelConfigData.json
 
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
