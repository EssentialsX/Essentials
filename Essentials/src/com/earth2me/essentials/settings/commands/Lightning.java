package com.earth2me.essentials.settings.commands;

import com.earth2me.essentials.storage.Comment;
import com.earth2me.essentials.storage.StorageObject;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class Lightning extends StorageObject
{
	@Comment("Shall we notify users when using /lightning")
	private boolean warnPlayer = true;
}
