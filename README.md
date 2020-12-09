# Sign Editor API
An API that provides the ability to use custom sign editor in **Spigot** servers.

- [Javadocs](https://sign-editor-api.ihdeveloper.me)
- [Discord](https://discord.gg/PYjh5xg)

## Requirements
- [PacketListenerAPI](https://github.com/InventivetalentDev/PacketListenerAPI) by @InventivetalentDev

## Example
```java
/* Default lines that shows to the player */
final String[] defaultLines = {
    "", /* Where the player is going to write */
    "===========",
    "This is",
    "Test Input"
};

SignEditorAPI.open(player, (player, lines) -> {
    System.out.println("Submitted Input: " + lines[0]);
}, defaultLines);
```
![image](https://user-images.githubusercontent.com/20463031/101609486-11f7a300-3a18-11eb-9463-11b97a9bf217.png)

## Getting Started
1. Add the API plugin to the your `plugin.yml` dependencies
```yml
depend:
  - sign-editor-api
```
2. You are ready to use the API! **\o/** Take a look at [Example](#Example) 
