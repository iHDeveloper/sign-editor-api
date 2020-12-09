# Sign Editor API
An API that provides the ability to use custom sign editor in **Spigot** servers.

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

## Getting Started
1. Add the API plugin to the your `plugin.yml` dependencies
```yml
depend:
  - sign-editor-api
```
2. You are ready to use the API! **\o/** Take a look at [Example](#Example) 
