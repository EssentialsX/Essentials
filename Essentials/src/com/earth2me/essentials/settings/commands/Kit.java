package com.earth2me.essentials.settings.commands;

import com.earth2me.essentials.storage.MapType;
import com.earth2me.essentials.storage.StorageObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class Kit extends StorageObject
{

	public Kit()
	{
		final KitObject kit = new KitObject();
		kit.setDelay(10.0);
		kit.setItems(Arrays.asList("277 1,278 1,279 1".split(",")));
		kits.put("tools", kit);
	}
	
	
	@MapType(KitObject.class)
	private Map<String,KitObject> kits = new HashMap<String, KitObject>();
}
