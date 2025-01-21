# Attack Modifier Basics

### This plugin is basically a cosmetic-only simple implementation of how this could work.
 
 You may get hit by your own attacks.
 
 Preview by Moistcrafter:
 
 https://user-images.githubusercontent.com/107363768/245295660-291fa040-4a0a-4c7b-8914-e18eb7da08df.mp4

## Installation
**<ins>Prebuilt JAR (Recommended):</ins>** 
- Get latest AttackModifier.jar release from [releases](https://github.com/NotThorny/AttackModifier/releases) and place it in your `\grasscutter\plugins` folder.
- Restart Grasscutter if it was already running.
- See [usage](https://github.com/NotThorny/AttackModifier#usage) for how to use the plugin in-game.

**Building from source (For advanced users):**

Building from source is for if you wish to build the plugin from source code yourself. If you are a normal user, please follow the prebuilt jar instruction above.
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
 "ayaka": {
    "skill": {
      "normalAtk": 0,
      "elementalSkill": 0,
      "elementalBurst": 0
    }
  }
 ```
