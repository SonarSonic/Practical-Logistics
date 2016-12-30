package sonar.logistics.info;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import sonar.core.utils.Pair;
import sonar.logistics.Logistics;
import sonar.logistics.api.info.ICustomEntityHandler;
import sonar.logistics.api.info.ICustomTileHandler;
import sonar.logistics.api.info.IInfoRegistry;
import sonar.logistics.api.info.LogicInfo;

/**where all the registering for LogicInfo happens*/
public class LogicInfoRegistry {

	/** the cache of methods/fields applicable to a given tile.*/
	public static LinkedHashMap<Class<?>, ArrayList<Method>> cachedMethods = new LinkedHashMap();
	public static LinkedHashMap<Class<?>, ArrayList<Field>> cachedFields = new LinkedHashMap();

	/** all the registries which can provide valid returns, methods and fields*/
	public static ArrayList<IInfoRegistry> infoRegistries = new ArrayList();

	/** all custom handlers which can provide custom info on blocks for tricky situations*/
	public static ArrayList<ICustomTileHandler> customTileHandlers = new ArrayList();
	public static ArrayList<ICustomEntityHandler> customEntityHandlers = new ArrayList();

	/** all the register validated returns, methods and fields from the registries*/
	public static ArrayList<Class<?>> registeredReturnTypes = Lists.newArrayList();
	public static LinkedHashMap<RegistryType, LinkedHashMap<Class<?>, ArrayList<Method>>> infoMethods = new LinkedHashMap();
	public static LinkedHashMap<RegistryType, LinkedHashMap<Class<?>, ArrayList<Field>>> infoFields = new LinkedHashMap();
	public static LinkedHashMap<Class<?>, Map<String, Integer>> invFields = new LinkedHashMap();
	public static LinkedHashMap<String, Pair<String, String>> infoAdjustments = new LinkedHashMap();

	/** the default accepted returns*/
	public static ArrayList<Class<?>> acceptedTypes = RegistryType.buildArrayList();
	public static ArrayList<Class<?>> defaultReturnTypes = Lists.newArrayList(String.class);

	/** used to define the type of class the method/return is applicable for this is to speed up identification but you can use NONE for any type of class if you wish */
	public static enum RegistryType {
		WORLD(World.class, 0), TILE(TileEntity.class, 5), BLOCK(Block.class, 3), ENTITY(Entity.class, 6), ITEM(Item.class, 7), STATE(IBlockState.class, 4), POS(BlockPos.class, 1), FACE(EnumFacing.class, 2), NONE(null, 8);
		Class classType;
		public int sortOrder;

		RegistryType(Class classType, int sortOrder) {
			this.classType = classType;
			this.sortOrder = sortOrder;
		}

		public boolean isAssignable(Class<?> toCheck) {
			return classType != null && classType.isAssignableFrom(toCheck);
		}

		public static RegistryType getRegistryType(Class<?> toCheck) {
			for (RegistryType type : values()) {
				if (type.isAssignable(toCheck)) {
					return type;
				}
			}
			return NONE;
		}

		public static ArrayList<Class<?>> buildArrayList() {
			ArrayList<Class<?>> classes = new ArrayList();
			for (RegistryType type : values()) {
				if (type.classType != null) {
					classes.add(type.classType);
				}
			}
			return classes;
		}
	}

	public static void init() {
		infoRegistries.forEach(registry -> registry.registerBaseReturns());
		infoRegistries.forEach(registry -> registry.registerBaseMethods());
		infoRegistries.forEach(registry -> registry.registerAllFields());
		infoRegistries.forEach(registry -> registry.registerAdjustments());
	}
	
	public static void reload(){
		registeredReturnTypes.clear();
		infoMethods.clear();
		infoFields.clone();
		invFields.clear();
		infoAdjustments.clear();
		
		init();
		
		cachedFields.clear();
		cachedMethods.clear();
	}

	/* public static void registerInfoHandlers() { infoRegistries.add(new VanillaInfoRegistry()); registerInfoRegistry("calculator", new CalculatorInfoRegistry()); } */
	public static void registerInfoRegistry(String modid, IInfoRegistry handler) {
		if (Loader.isModLoaded(modid)) {
			infoRegistries.add(handler);
		}
	}

	public static void registerReturn(Class<?> classType) {
		registeredReturnTypes.add(classType);
	}

	public static void registerMethods(Class<?> classType, RegistryType type) {
		registerMethods(classType, type, Lists.newArrayList());
	}

	public static void registerMethods(Class<?> classType, RegistryType type, ArrayList<String> methodNames) {
		registerMethods(classType, type, methodNames, false);
	}

	public static void registerMethods(Class<?> classType, RegistryType type, ArrayList<String> methodNames, boolean exclude) {
		infoMethods.putIfAbsent(type, new LinkedHashMap());
		infoMethods.get(type).putIfAbsent(classType, new ArrayList());
		ArrayList<String> used = new ArrayList();
		Method[] methods = classType.getMethods();
		for (Method method : methods) {
			if (!used.contains(method.getName()) && (methodNames.isEmpty() || (exclude ? !methodNames.contains(method.getName()) : methodNames.contains(method.getName())))) {
				boolean validParams = validateParameters(method.getParameterTypes()), validReturns = isValidReturnType(method.getReturnType());
				if (validParams && validReturns) {
					infoMethods.get(type).get(classType).add(method);
					used.add(method.getName());
				} else {
					Logistics.logger.error(String.format("Failed to load method: %s, Valid Parameters: %s, Valid Returns %s,", method.toString(), validParams, validReturns));
				}
			}
		}
	}

	public static void registerFields(Class<?> classType, RegistryType type) {
		registerFields(classType, type, Lists.newArrayList());
	}

	public static void registerFields(Class<?> classType, RegistryType type, ArrayList<String> fieldNames) {
		registerFields(classType, type, fieldNames, false);
	}

	public static void registerFields(Class<?> classType, RegistryType type, ArrayList<String> fieldNames, boolean exclude) {
		infoFields.putIfAbsent(type, new LinkedHashMap());
		infoFields.get(type).putIfAbsent(classType, new ArrayList());
		Field[] fields = classType.getFields();
		for (Field field : fields) {
			if ((fieldNames.isEmpty() || (exclude ? !fieldNames.contains(field.getName()) : fieldNames.contains(field.getName())))) {
				boolean validReturns = isValidReturnType(field.getType());
				if (validReturns) {
					infoFields.get(type).get(classType).add(field);
				} else {
					Logistics.logger.error(String.format("Failed to load field: %s, Valid Returns: %s,", field.toString(), validReturns));
				}
			}

		}
	}

	public static void registerInvFields(Class<?> inventoryClass, Map<String, Integer> fields) {
		invFields.put(inventoryClass, fields);
	}

	public static void registerInfoAdjustments(ArrayList<String> identifiers, String prefix, String suffix) {
		identifiers.forEach(identifier -> infoAdjustments.put(identifier, new Pair(prefix, suffix)));
	}

	public static void registerInfoAdjustments(String identifier, String prefix, String suffix) {
		infoAdjustments.put(identifier, new Pair(prefix, suffix));
	}

	public static boolean containsAssignableType(Class<?> toCheck, ArrayList<Class<?>> classes) {
		for (Class<?> cls : classes) {
			if (cls.isAssignableFrom(toCheck) || toCheck.isAssignableFrom(cls)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isValidReturnType(Class<?> returnType) {
		return returnType.isPrimitive() || containsAssignableType(returnType, defaultReturnTypes) || containsAssignableType(returnType, registeredReturnTypes) || containsAssignableType(returnType, acceptedTypes);
	}

	public static boolean validateParameters(Class<?>[] parameters) {
		if (parameters.length == 0) {
			return true;
		}
		for (Class<?> param : parameters) {
			if (!containsAssignableType(param, acceptedTypes)) {
				return false;
			}
		}
		return true;
	}

	public static ArrayList<Method> getAssignableMethods(Class<?> obj, RegistryType type) {
		ArrayList<Method> methods = cachedMethods.get(obj);

		if (methods == null) {
			methods = new ArrayList();
			LinkedHashMap<Class<?>, ArrayList<Method>> map = infoMethods.getOrDefault(type, new LinkedHashMap());
			if (type == RegistryType.NONE) {
				map.putAll(infoMethods.get(RegistryType.NONE));
			}
			for (Entry<Class<?>, ArrayList<Method>> classTypes : map.entrySet()) {
				if (classTypes.getKey().isAssignableFrom(obj) || obj.isAssignableFrom(classTypes.getKey())) {
					methods.addAll(classTypes.getValue());
				}
			}
			cachedMethods.put(obj, methods);
		}
		return methods;
	}

	public static ArrayList<Field> getAccessibleFields(Class<?> obj, RegistryType type) {
		ArrayList<Field> fields = cachedFields.get(obj);
		if (fields == null) {
			fields = new ArrayList();
			LinkedHashMap<Class<?>, ArrayList<Field>> map = infoFields.getOrDefault(type, new LinkedHashMap());
			if (type == RegistryType.NONE) {
				map.putAll(infoFields.get(RegistryType.NONE));
			}
			for (Entry<Class<?>, ArrayList<Field>> classTypes : map.entrySet()) {
				if (classTypes.getKey().isAssignableFrom(obj)) {
					fields.addAll(classTypes.getValue());
				}
			}
			cachedFields.put(obj, fields);
		}
		return fields;
	}

	public static Object invokeMethod(Object obj, Method method, Object... available) {
		Class<?>[] params = method.getParameterTypes();
		Object[] inputs = new Object[params.length];
		for (int i = 0; i < params.length; i++) {
			Class<?> param = params[i];
			for (Object arg : available) {
				if (param.isInstance(arg)) {
					inputs[i] = arg;
					break;
				}
			}
		}
		for (Object input : inputs) {
			if (input == null) {
				return null;
			}
		}
		try {
			return method.invoke(obj, inputs);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			Logistics.logger.error("COULDN'T INVOKE METHOD!" + method + " on object" + obj);
		}
		return null;

	}

	public static void getClassInfo(List<LogicInfo> infoList, RegistryType type, Object obj, Method method, Object... available) {
		Object returned = invokeMethod(obj, method, available);
		if (returned == null)
			return;
		Class<?> returnedClass = returned.getClass();
		if (!returnedClass.isPrimitive() && !containsAssignableType(returnedClass, defaultReturnTypes) && containsAssignableType(returnedClass, registeredReturnTypes)) {
			getAssignableMethods(returnedClass, type).forEach(returnMethod -> getClassInfo(infoList, type, returned, returnMethod, available));
		} else {
			buildInfo(infoList, method.getDeclaringClass().getSimpleName(), method.getName(), type, returned);
		}
	}

	/** @param infoList the list to add to
	 * @param type the Registry Type to get the field from
	 * @param obj the object to get the field from
	 * @param field the field itself
	 * @param available all available info about the tile, typically will include the World, BlockPos, IBlockState, EnumFacing, the Block and the tile entity */
	public static void getFieldInfo(List<LogicInfo> infoList, RegistryType type, Object obj, Field field, Object... available) {
		Object fieldObj = getField(obj, field);
		if (fieldObj == null)
			return;
		Class<?> returnedClass = fieldObj.getClass();
		if (!returnedClass.isPrimitive() && !containsAssignableType(returnedClass, defaultReturnTypes) && containsAssignableType(returnedClass, registeredReturnTypes)) {
			getAssignableMethods(returnedClass, type).forEach(returnMethod -> getClassInfo(infoList, type, fieldObj, returnMethod, available));
		} else {
			buildInfo(infoList, field.getDeclaringClass().getSimpleName(), field.getName(), type, fieldObj);
		}
	}

	/** @param infoList the list to add to
	 * @param className the name of the class
	 * @param fieldName the name of the method or field
	 * @param object the object returned, this will never be null and will be of compatible type */
	public static void buildInfo(List<LogicInfo> infoList, String className, String fieldName, RegistryType type, Object object) {
		LogicInfo info = LogicInfo.buildDirectInfo(className + "." + fieldName, type, object);
		if (info != null) {
			infoList.add(info);
		}
	}

	/** @param obj the object to get the field from
	 * @param field the field to obtain
	 * @return the fields object if there is one */
	public static Object getField(Object obj, Field field) {
		try {
			return field.get(obj);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** @param infoList the list to add to
	 * @param available all available info about the tile, typically will include the World, BlockPos, IBlockState, EnumFacing, the Block and the tile entity
	 * @return all the available info */
	public static List<LogicInfo> getTileInfo(final List<LogicInfo> infoList, Object... available) {
		for (Object arg : available) {
			Class<?> argClass;
			if (arg != null && containsAssignableType(argClass = arg.getClass(), acceptedTypes)) {
				RegistryType type = RegistryType.getRegistryType(argClass);
				getAssignableMethods(argClass, type).forEach(method -> getClassInfo(infoList, type, arg, method, available));
				getAccessibleFields(argClass, type).forEach(field -> getFieldInfo(infoList, type, arg, field));
				if (arg instanceof IInventory) {
					Map<String, Integer> fields = invFields.get(argClass);
					if (fields != null && !fields.isEmpty()) {
						fields.entrySet().forEach(field -> infoList.add(LogicInfo.buildDirectInfo(argClass.getSimpleName() + "." + field.getKey(), type, ((IInventory) arg).getField(field.getValue()))));
					}
				}
			}
		}
		return infoList;
	}
}
