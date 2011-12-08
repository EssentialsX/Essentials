package com.earth2me.essentials.user;

import com.earth2me.essentials.storage.StorageObject;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class Ban implements StorageObject
{
	private String reason;
	private long timeout;
}
