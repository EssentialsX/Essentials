package com.earth2me.essentials.settings.commands;


import com.earth2me.essentials.storage.Comment;
import com.earth2me.essentials.storage.StorageObject;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class List implements StorageObject
{
	@Comment("Sort output of /list command by groups")
	private boolean sortByGroups = true;
}
