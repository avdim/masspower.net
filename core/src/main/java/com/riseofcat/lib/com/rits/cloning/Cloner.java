package com.riseofcat.lib.com.rits.cloning;

import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.riseofcat.App;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

@SuppressWarnings({"ObjectEquality", "SuspiciousSystemArraycopy"}) public class Cloner {
private final Set<Class<?>> ignored = new HashSet<>();
private final Map<Class<?>, IFastCloner> fastCloners = new HashMap<>();
private final Map<Class<?>, List<Field>> fieldsCache = App.context.createConcurrentHashMap();//new ConcurrentHashMap<>();

private IDumpCloned dumpCloned = null;
private boolean cloningEnabled = true;
private boolean nullTransient = false;
private boolean cloneSynthetics = true;

public Cloner() {
	init();
}

private void init() {
	registerKnownJdkImmutableClasses();
	registerFastCloners();
}

/**
 * registers a std set of fast cloners.
 */
protected void registerFastCloners() {
//	fastCloners.put(GregorianCalendar.class, new FastClonerCalendar());
	fastCloners.put(ArrayList.class, new FastClonerArrayList());
	fastCloners.put(LinkedList.class, new FastClonerLinkedList());
	fastCloners.put(HashSet.class, new FastClonerHashSet());
	fastCloners.put(HashMap.class, new FastClonerHashMap());
	fastCloners.put(TreeMap.class, new FastClonerTreeMap());
	fastCloners.put(LinkedHashMap.class, new FastClonerLinkedHashMap());
}

private IDeepCloner deepCloner = new IDeepCloner() {
	public <T> T deepClone(T o, Map<Object, Object> clones) {
		try {
			return cloneInternal(o, clones);
		} catch(ReflectionException e) {
			throw new IllegalStateException(e);
		}
	}
};

protected Object fastClone(final Object o, final Map<Object, Object> clones) {
	final Class<?> c = o.getClass();
	final IFastCloner fastCloner = fastCloners.get(c);
	if(fastCloner != null) return fastCloner.clone(o, deepCloner, clones);
	return null;
}

/**
 * registers some known JDK immutable classes. Override this to register your
 * own list of jdk's immutable classes
 */
protected void registerKnownJdkImmutableClasses() {
	registerImmutable(String.class);
	registerImmutable(Integer.class);
	registerImmutable(Long.class);
	registerImmutable(Boolean.class);
	registerImmutable(Class.class);
	registerImmutable(Float.class);
	registerImmutable(Double.class);
	registerImmutable(Character.class);
	registerImmutable(Byte.class);
	registerImmutable(Short.class);
	registerImmutable(Void.class);

	registerImmutable(BigDecimal.class);
	registerImmutable(BigInteger.class);
//	registerImmutable(URI.class);
//	registerImmutable(URL.class);
//	registerImmutable(UUID.class);
	registerImmutable(Pattern.class);
}

/**
 * registers all static fields of these classes. Those static fields won't be cloned when an instance
 * of the class is cloned.
 * <p>
 * This is useful i.e. when a static field object is added into maps or sets. At that point, there is no
 * way for the cloner to know that it was static except if it is registered.
 *
 * @param classes array of classes
 */

/**
 * registers an immutable class. Immutable classes are not cloned.
 *
 * @param c the immutable class
 */
public void registerImmutable(final Class<?>... c) {
	Collections.addAll(ignored, c);
}

/**
 * creates a new instance of c. Override to provide your own implementation
 *
 * @param <T> the type of c
 * @param c   the class
 * @return a new instance of c
 */
protected <T> T newInstance(final Class<T> c) {
	try {
		return ClassReflection.newInstance(c);
	} catch(ReflectionException e) {
		e.printStackTrace();
	}
	return null;
//	return instantiationStrategy.newInstance(c);
}

/**
 * deep clones "o".
 *
 * @param <T> the type of "o"
 * @param o   the object to be deep-cloned
 * @return a deep-clone of "o".
 */
public <T> T deepClone(final T o) {
	if(o == null) return null;
	if(!cloningEnabled) return o;
	if(dumpCloned != null) {
		dumpCloned.startCloning(o.getClass());
	}
	final Map<Object, Object> clones = new IdentityHashMap<>(16);
	try {
		return cloneInternal(o, clones);
	} catch(ReflectionException e) {
		throw new CloningException("error during cloning of " + o, e);
	}
}

// caches immutables for quick reference
private final Map<Class<?>, Boolean> immutables = App.context.createConcurrentHashMap();
private boolean cloneAnonymousParent = true;

/**
 * override this to decide if a class is immutable. Immutable classes are not cloned.
 *
 * @return true to mark clz as immutable and skip cloning it
 */
protected boolean considerImmutable() {
	return false;
}

protected Class<?> getImmutableAnnotation() {
	return Immutable.class;
}

/**
 * decides if a class is to be considered immutable or not
 *
 * @param clz the class under check
 * @return true if the clz is considered immutable
 */
private boolean isImmutable(final Class<?> clz) {
	final Boolean isIm = immutables.get(clz);
	if(isIm != null) return isIm;
	if(considerImmutable()) return true;

	final Class<?> immutableAnnotation = getImmutableAnnotation();
	for(final com.badlogic.gdx.utils.reflect.Annotation annotation : ClassReflection.getDeclaredAnnotations(clz)) {
		if(annotation.getAnnotationType() == immutableAnnotation) {
			immutables.put(clz, Boolean.TRUE);
			return true;
		}
	}
	Class<?> c = clz.getSuperclass();
	while(c != null && c != Object.class) {
		for(final com.badlogic.gdx.utils.reflect.Annotation annotation : ClassReflection.getDeclaredAnnotations(c)) {
			if(annotation.getAnnotationType() == Immutable.class) {
				final Immutable im = (Immutable) annotation.getAnnotation(annotation.getAnnotationType());
				if(im.subClass()) {
					immutables.put(clz, Boolean.TRUE);
					return true;
				}
			}
		}
		c = c.getSuperclass();
	}
	immutables.put(clz, Boolean.FALSE);
	return false;
}

@SuppressWarnings("unchecked")
protected <T> T cloneInternal(final T o, final Map<Object, Object> clones) throws ReflectionException {
	if(o == null) return null;
	if(o == this) return null; // don't clone the cloner!
	if(o instanceof Enum) return o;
	final Class<T> clz = (Class<T>) o.getClass();
	// skip cloning ignored classes
	if(ignored.contains(clz)) return o;
	if(isImmutable(clz)) return o;
	if(o instanceof IFreezable) {
		final IFreezable f = (IFreezable) o;
		if(f.isFrozen()) return o;
	}
	final Object clonedPreviously = clones != null ? clones.get(o) : null;
	if(clonedPreviously != null) return (T) clonedPreviously;

	final Object fastClone = fastClone(o, clones);
	if(fastClone != null) {
		if(clones != null) {
			clones.put(o, fastClone);
		}
		return (T) fastClone;
	}

	if(dumpCloned != null) {
		dumpCloned.startCloning(o.getClass());
	}
	if(clz.isArray()) {
		return cloneArray(o, clones);
	}

	return cloneObject(o, clones, clz);
}

// clones o, no questions asked!
private <T> T cloneObject(T o, Map<Object, Object> clones, Class<T> clz) throws ReflectionException {
	final T newInstance = newInstance(clz);
	if(clones != null) {
		clones.put(o, newInstance);
	}
	final List<Field> fields = allFields(clz);
	for(final Field field : fields) {
		if(!(nullTransient /*&& (true || Modifier.isTransient(modifiers))*/ )) {
			// request by Jonathan : transient fields can be `null-ed
			final Object fieldObject = field.get(o);
			final boolean shouldClone = (cloneSynthetics || !field.isSynthetic()) && (cloneAnonymousParent || !isAnonymousParent(field));
			final Object fieldObjectClone = clones != null ? (shouldClone ? cloneInternal(fieldObject, clones) : fieldObject) : fieldObject;
			field.set(newInstance, fieldObjectClone);
			if(dumpCloned != null && fieldObjectClone != fieldObject) {
				dumpCloned.cloning(field, o.getClass());
			}
		}

	}
	return newInstance;
}

@SuppressWarnings("unchecked")
private <T> T cloneArray(T o, Map<Object, Object> clones) throws ReflectionException {
	final Class<T> clz = (Class<T>) o.getClass();
	final int length = ArrayReflection.getLength(o);
	final T newInstance = (T) ArrayReflection.newInstance(clz.getComponentType(), length);
	if(clones != null) {
		clones.put(o, newInstance);
	}
	if(clz.getComponentType().isPrimitive() || isImmutable(clz.getComponentType())) {
		System.arraycopy(o, 0, newInstance, 0, length);
	} else {
		for(int i = 0; i < length; i++) {
			final Object v = ArrayReflection.get(o, i);
			final Object clone = clones != null ? cloneInternal(v, clones) : v;
			ArrayReflection.set(newInstance, i, clone);
		}
	}
	return newInstance;
}

private boolean isAnonymousParent(final Field field) {
	return "this$0".equals(field.getName());
}

/**
 * reflection utils
 */
private void addAll(final List<Field> l, final Field[] fields) {
	for(final Field field : fields) {
		if(!field.isAccessible()) {
			field.setAccessible(true);
		}
		l.add(field);
	}
}

/**
 * reflection utils, override this to choose which fields to clone
 */
protected List<Field> allFields(final Class<?> c) {
	List<Field> l = fieldsCache.get(c);
	if(l == null) {
		l = new LinkedList<>();
		final Field[] fields = ClassReflection.getDeclaredFields(c);
		addAll(l, fields);
		Class<?> sc = c;
		while((sc = sc.getSuperclass()) != Object.class && sc != null) {
			addAll(l, ClassReflection.getDeclaredFields(sc));
		}
		if(!fieldsCache.containsKey(c)) {
			fieldsCache.put(c, l);
		}
	}
	return l;
}

}
