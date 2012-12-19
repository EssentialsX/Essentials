package com.earth2me.essentials.storage;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.*;


public class BukkitConstructor extends Constructor
{
	private final transient Pattern NUMPATTERN = Pattern.compile("\\d+");
	private final transient Plugin plugin;

	public BukkitConstructor(final Class clazz, final Plugin plugin)
	{
		super(clazz);
		this.plugin = plugin;
		yamlClassConstructors.put(NodeId.scalar, new ConstructBukkitScalar());
		yamlClassConstructors.put(NodeId.mapping, new ConstructBukkitMapping());
	}


	private class ConstructBukkitScalar extends ConstructScalar
	{
		@Override
		public Object construct(final Node node)
		{
			if (node.getType().equals(Material.class))
			{
				final String val = (String)constructScalar((ScalarNode)node);
				Material mat;
				if (NUMPATTERN.matcher(val).matches())
				{
					final int typeId = Integer.parseInt(val);
					mat = Material.getMaterial(typeId);
				}
				else
				{
					mat = Material.matchMaterial(val);
				}
				return mat;
			}
			if (node.getType().equals(MaterialData.class))
			{
				final String val = (String)constructScalar((ScalarNode)node);
				if (val.isEmpty())
				{
					return null;
				}
				final String[] split = val.split("[:+',;.]", 2);
				if (split.length == 0)
				{
					return null;
				}
				Material mat;
				if (NUMPATTERN.matcher(split[0]).matches())
				{
					final int typeId = Integer.parseInt(split[0]);
					mat = Material.getMaterial(typeId);
				}
				else
				{
					mat = Material.matchMaterial(split[0]);
				}
				if (mat == null)
				{
					return null;
				}
				byte data = 0;
				if (split.length == 2 && NUMPATTERN.matcher(split[1]).matches())
				{
					data = Byte.parseByte(split[1]);
				}
				return new MaterialData(mat, data);
			}
			if (node.getType().equals(ItemStack.class))
			{
				final String val = (String)constructScalar((ScalarNode)node);
				if (val.isEmpty())
				{
					return null;
				}
				final String[] split1 = val.split("\\W");
				if (split1.length == 0)
				{
					return null;
				}
				final String[] split2 = split1[0].split("[:+',;.]", 2);
				if (split2.length == 0)
				{
					return null;
				}
				Material mat;
				if (NUMPATTERN.matcher(split2[0]).matches())
				{
					final int typeId = Integer.parseInt(split2[0]);
					mat = Material.getMaterial(typeId);
				}
				else
				{
					mat = Material.matchMaterial(split2[0]);
				}
				if (mat == null)
				{
					return null;
				}
				short data = 0;
				if (split2.length == 2 && NUMPATTERN.matcher(split2[1]).matches())
				{
					data = Short.parseShort(split2[1]);
				}
				int size = mat.getMaxStackSize();
				if (split1.length > 1 && NUMPATTERN.matcher(split1[1]).matches())
				{
					size = Integer.parseInt(split1[1]);
				}
				final ItemStack stack = new ItemStack(mat, size, data);
				if (split1.length > 2)
				{
					for (int i = 2; i < split1.length; i++)
					{
						final String[] split3 = split1[0].split("[:+',;.]", 2);
						if (split3.length < 1)
						{
							continue;
						}
						Enchantment enchantment;
						if (NUMPATTERN.matcher(split3[0]).matches())
						{
							final int enchantId = Integer.parseInt(split3[0]);
							enchantment = Enchantment.getById(enchantId);
						}
						else
						{
							enchantment = Enchantment.getByName(split3[0].toUpperCase(Locale.ENGLISH));
						}
						if (enchantment == null)
						{
							continue;
						}
						int level = enchantment.getStartLevel();
						if (split3.length == 2 && NUMPATTERN.matcher(split3[1]).matches())
						{
							level = Integer.parseInt(split3[1]);
						}
						if (level < enchantment.getStartLevel())
						{
							level = enchantment.getStartLevel();
						}
						if (level > enchantment.getMaxLevel())
						{
							level = enchantment.getMaxLevel();
						}
						stack.addUnsafeEnchantment(enchantment, level);
					}
				}
				return stack;
			}
			if (node.getType().equals(EnchantmentLevel.class))
			{
				final String val = (String)constructScalar((ScalarNode)node);
				if (val.isEmpty())
				{
					return null;
				}
				final String[] split = val.split("[:+',;.]", 2);
				if (split.length == 0)
				{
					return null;
				}
				Enchantment enchant;
				if (NUMPATTERN.matcher(split[0]).matches())
				{
					final int typeId = Integer.parseInt(split[0]);
					enchant = Enchantment.getById(typeId);
				}
				else
				{
					enchant = Enchantment.getByName(split[0].toUpperCase(Locale.ENGLISH));
				}
				if (enchant == null)
				{
					return null;
				}
				int level = enchant.getStartLevel();
				if (split.length == 2 && NUMPATTERN.matcher(split[1]).matches())
				{
					level = Integer.parseInt(split[1]);
				}
				if (level < enchant.getStartLevel())
				{
					level = enchant.getStartLevel();
				}
				if (level > enchant.getMaxLevel())
				{
					level = enchant.getMaxLevel();
				}
				return new EnchantmentLevel(enchant, level);
			}
			return super.construct(node);
		}
	}


	private class ConstructBukkitMapping extends ConstructMapping
	{
		@Override
		public Object construct(final Node node)
		{
			if (node.getType().equals(Location.class))
			{
				//TODO: NPE checks
				final MappingNode mnode = (MappingNode)node;
				String worldName = "";
				double x = 0, y = 0, z = 0;
				float yaw = 0, pitch = 0;
				if (mnode.getValue().size() < 4)
				{
					return null;
				}
				for (NodeTuple nodeTuple : mnode.getValue())
				{
					final String key = (String)constructScalar((ScalarNode)nodeTuple.getKeyNode());
					final ScalarNode snode = (ScalarNode)nodeTuple.getValueNode();
					if (key.equalsIgnoreCase("world"))
					{
						worldName = (String)constructScalar(snode);
					}
					if (key.equalsIgnoreCase("x"))
					{
						x = Double.parseDouble((String)constructScalar(snode));
					}
					if (key.equalsIgnoreCase("y"))
					{
						y = Double.parseDouble((String)constructScalar(snode));
					}
					if (key.equalsIgnoreCase("z"))
					{
						z = Double.parseDouble((String)constructScalar(snode));
					}
					if (key.equalsIgnoreCase("yaw"))
					{
						yaw = Float.parseFloat((String)constructScalar(snode));
					}
					if (key.equalsIgnoreCase("pitch"))
					{
						pitch = Float.parseFloat((String)constructScalar(snode));
					}
				}
				if (worldName == null || worldName.isEmpty())
				{
					return null;
				}
				final World world = Bukkit.getWorld(worldName);
				if (world == null)
				{
					return null;
				}
				return new Location(world, x, y, z, yaw, pitch);
			}
			return super.construct(node);
		}

		protected Object constructJavaBean2ndStep(final MappingNode node, final Object object)
		{
			Map<Class<? extends Object>, TypeDescription> typeDefinitions;
			try
			{
				final Field typeDefField = Constructor.class.getDeclaredField("typeDefinitions");
				typeDefField.setAccessible(true);
				typeDefinitions = (Map<Class<? extends Object>, TypeDescription>)typeDefField.get((Constructor)BukkitConstructor.this);
				if (typeDefinitions == null)
				{
					throw new NullPointerException();
				}
			}
			catch (Exception ex)
			{
				throw new YAMLException(ex);
			}
			flattenMapping(node);
			final Class<? extends Object> beanType = node.getType();
			final List<NodeTuple> nodeValue = node.getValue();
			for (NodeTuple tuple : nodeValue)
			{
				ScalarNode keyNode;
				if (tuple.getKeyNode() instanceof ScalarNode)
				{
					// key must be scalar
					keyNode = (ScalarNode)tuple.getKeyNode();
				}
				else
				{
					throw new YAMLException("Keys must be scalars but found: " + tuple.getKeyNode());
				}
				final Node valueNode = tuple.getValueNode();
				// keys can only be Strings
				keyNode.setType(String.class);
				final String key = (String)constructObject(keyNode);
				try
				{
					Property property;
					try
					{
						property = getProperty(beanType, key);
					}
					catch (YAMLException e)
					{
						continue;
					}
					valueNode.setType(property.getType());
					final TypeDescription memberDescription = typeDefinitions.get(beanType);
					boolean typeDetected = false;
					if (memberDescription != null)
					{
						switch (valueNode.getNodeId())
						{
						case sequence:
							final SequenceNode snode = (SequenceNode)valueNode;
							final Class<? extends Object> memberType = memberDescription.getListPropertyType(key);
							if (memberType != null)
							{
								snode.setListType(memberType);
								typeDetected = true;
							}
							else if (property.getType().isArray())
							{
								snode.setListType(property.getType().getComponentType());
								typeDetected = true;
							}
							break;
						case mapping:
							final MappingNode mnode = (MappingNode)valueNode;
							final Class<? extends Object> keyType = memberDescription.getMapKeyType(key);
							if (keyType != null)
							{
								mnode.setTypes(keyType, memberDescription.getMapValueType(key));
								typeDetected = true;
							}
							break;
						}
					}
					if (!typeDetected && valueNode.getNodeId() != NodeId.scalar)
					{
						// only if there is no explicit TypeDescription
						final Class<?>[] arguments = property.getActualTypeArguments();
						if (arguments != null)
						{
							// type safe (generic) collection may contain the
							// proper class
							if (valueNode.getNodeId() == NodeId.sequence)
							{
								final Class<?> t = arguments[0];
								final SequenceNode snode = (SequenceNode)valueNode;
								snode.setListType(t);
							}
							else if (valueNode.getTag().equals(Tag.SET))
							{
								final Class<?> t = arguments[0];
								final MappingNode mnode = (MappingNode)valueNode;
								mnode.setOnlyKeyType(t);
								mnode.setUseClassConstructor(true);
							}
							else if (property.getType().isAssignableFrom(Map.class))
							{
								final Class<?> ketType = arguments[0];
								final Class<?> valueType = arguments[1];
								final MappingNode mnode = (MappingNode)valueNode;
								mnode.setTypes(ketType, valueType);
								mnode.setUseClassConstructor(true);
							}
							else
							{
								// the type for collection entries cannot be
								// detected
							}
						}
					}
					final Object value = constructObject(valueNode);
					property.set(object, value);
				}
				catch (Exception e)
				{
					throw new YAMLException("Cannot create property=" + key + " for JavaBean="
											+ object + "; " + e.getMessage(), e);
				}
			}
			return object;
		}
	}
}
