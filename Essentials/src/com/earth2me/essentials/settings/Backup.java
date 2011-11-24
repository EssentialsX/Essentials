package com.earth2me.essentials.settings;

import com.earth2me.essentials.storage.Comment;
import com.earth2me.essentials.storage.StorageObject;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class Backup implements StorageObject
{
	@Comment("Interval in minutes")
	private long interval = 60;
	@Comment("Add a command that backups your data, e.g. 'rdiff-backup World1 backups/World1'")
	private String command;
}
