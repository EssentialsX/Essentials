package com.earth2me.essentials.settings;

import com.earth2me.essentials.storage.Comment;
import com.earth2me.essentials.storage.StorageObject;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class Chat implements StorageObject
{
	@Comment("The character(s) to prefix all nicknames, so that you know they are not true usernames.")
	private String nicknamePrefix = "~";
	@Comment(
	{
		"Disable this if you have any other plugin, that modifies the displayname of a user.",
		"If it is not set, it will be enabled if EssentialsChat is installed, otherwise not."
	})
	private Boolean changeDisplayname = true;
	private String displaynameFormat = "{PREFIX}{NICKNAMEPREFIX}{NAME}{SUFFIX}";
	@Comment(
	{
		"If EssentialsChat is installed, this will define how far a player's voice travels, in blocks.  Set to 0 to make all chat global.",
		"Note that users with the \"essentials.chat.spy\" permission will hear everything, regardless of this setting.",
		"Users with essentials.chat.shout can override this by prefixing text with an exclamation mark (!)",
		"Or with essentials.chat.question can override this by prefixing text with a question mark (?)",
		"You can add command costs for shout/question by adding chat-shout and chat-question to the command costs section."
	})
	private int localRadius = 0;
	@Comment("Set the default chat format here, it will be overwritten by group specific chat formats.")
	private String defaultFormat = "&7[{GROUP}]&f {DISPLAYNAME}&7:&f {MESSAGE}";
}
