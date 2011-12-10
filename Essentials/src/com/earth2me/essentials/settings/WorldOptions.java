package com.earth2me.essentials.settings;

import com.earth2me.essentials.storage.Comment;
import com.earth2me.essentials.storage.StorageObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class WorldOptions implements StorageObject
{
	@Comment("Disables godmode for all players if they teleport to this world.")
	private boolean godmode = true;
}
