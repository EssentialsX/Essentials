package com.earth2me.essentials.settings.commands;

import com.earth2me.essentials.storage.ListType;
import com.earth2me.essentials.storage.StorageObject;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class KitObject extends StorageObject
{
	@ListType
	private List<String> items = new ArrayList<String>();
	private Double delay;
}
