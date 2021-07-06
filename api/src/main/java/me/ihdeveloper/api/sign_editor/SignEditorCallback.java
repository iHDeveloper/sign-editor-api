package me.ihdeveloper.api.sign_editor;

import org.bukkit.entity.Player;

/**
 * A callback that gets triggered when an event is being fired.
 * When the player is doing something with the sign editor
 */
public interface SignEditorCallback {

    /**
     * Triggered when the player clicks "Done" in the sign editor
     *
     * @param player Who clicked "Done" on the editor
     * @param lines The lines that were submitted by the player
     */
    void onSubmit(Player player, String[] lines);
}
