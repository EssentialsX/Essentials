package com.earth2me.essentials.settings.commands;

import com.earth2me.essentials.storage.Comment;
import com.earth2me.essentials.storage.StorageObject;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class Tpa implements StorageObject
{
	@Comment(
	{
		"Set timeout in seconds for players to accept tpa before request is cancelled.",
		"Set to 0 for no timeout."
	})
	private int timeout = 0;
	@Comment({"Cancels a request made by tpa / tphere on world change to prevent cross world tp"})	
    private boolean cancelTpRequestsOnWorldChange = false;
}
