package com.earth2me.essentials.settings.protect;

import com.earth2me.essentials.storage.Comment;
import com.earth2me.essentials.storage.StorageObject;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class SignsAndRails implements StorageObject
{
	@Comment("Protect all signs")
	private boolean protectSigns = true;
	@Comment("Prevent users from destroying rails")
	private boolean protectRails = true;
	@Comment(
	{
		"Blocks below rails/signs are also protected if the respective rail/sign is protected.",
		"This makes it more difficult to circumvent protection, and should be enabled.",
		"This only has an effect if rails or signs is also enabled."
	})
	private boolean blockBelow = true;
	@Comment("Prevent placing blocks above protected rails, this is to stop a potential griefing")
	private boolean preventBlockAboveRails = false;
}
