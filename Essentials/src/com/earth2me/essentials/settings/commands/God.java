package com.earth2me.essentials.settings.commands;

import com.earth2me.essentials.storage.Comment;
import com.earth2me.essentials.storage.StorageObject;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class God extends StorageObject
{
	@Comment("Turn off god mode when people exit")
	private boolean removeOnDisconnect = false;
}
