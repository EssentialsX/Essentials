package com.earth2me.essentials.settings.commands;

import com.earth2me.essentials.storage.Comment;
import com.earth2me.essentials.storage.StorageObject;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class Help extends StorageObject
{
	@Comment("Show other plugins commands in help")
	private boolean showNonEssCommandsInHelp = true;
	@Comment(
	{
		"Hide plugins which don't give a permission in their plugin.yml for each command.",
		"You can override a true value here for a single plugin by adding a permission to a user/group.",
		"The individual permission is: essentials.help.<plugin>, anyone with essentials.* or '*' will see all help this setting reguardless.",
		"You can use negative permissions to remove access to just a single plugins help if the following is enabled."
	})
	private boolean hidePermissionlessCommands = true;
}
