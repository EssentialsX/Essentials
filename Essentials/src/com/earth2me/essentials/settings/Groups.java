package com.earth2me.essentials.settings;

import com.earth2me.essentials.storage.Comment;
import com.earth2me.essentials.storage.MapType;
import com.earth2me.essentials.storage.StorageObject;
import java.util.LinkedHashMap;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class Groups extends StorageObject
{	
	public Groups() {
		GroupOptions defaultOptions = new GroupOptions();
		groups.put("default", defaultOptions);
	}
	@Comment(
	{
		"The order of the groups matters, the groups are checked from top to bottom.",
		"All group names have to be lower case.",
		"The groups can be connected to users using the permission essentials.groups.groupname"
	})
	@MapType(GroupOptions.class)
	private LinkedHashMap<String, GroupOptions> groups = new LinkedHashMap<String, GroupOptions>();
}
