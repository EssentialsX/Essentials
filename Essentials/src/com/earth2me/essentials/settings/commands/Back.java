package com.earth2me.essentials.settings.commands;

import com.earth2me.essentials.storage.Comment;
import com.earth2me.essentials.storage.StorageObject;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class Back implements StorageObject
{
	@Comment(
	{
		"Do you want essentials to keep track of previous location for /back in the teleport listener?",
		"If you set this to true any plugin that uses teleport will have the previous location registered."
	})
	private boolean registerBackInListener = false;
}
