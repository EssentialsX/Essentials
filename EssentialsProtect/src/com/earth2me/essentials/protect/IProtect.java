package com.earth2me.essentials.protect;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.protect.data.IProtectedBlock;


public interface IProtect
{
	void alert(final User user, final String item, final String type);

	boolean checkProtectionItems(final ProtectConfig list, final int id);

	boolean getSettingBool(final ProtectConfig protectConfig);

	String getSettingString(final ProtectConfig protectConfig);

	IProtectedBlock getStorage();

	IEssentials getEssentials();
}
