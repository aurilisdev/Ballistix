package ballistix.compatibility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public final class TessellateCompat {

    private static final Method EXECUTE_ON_REGION = load();
    private static final Method EXECUTE_ON_MAIN = load("executeOnMainThread", Runnable.class);

    private TessellateCompat() {
    }

    public static boolean isLoaded() {
	return EXECUTE_ON_REGION != null;
    }

    public static void runOnRegion(ServerLevel level, BlockPos pos, Runnable work) {
	if (EXECUTE_ON_REGION == null) {
	    work.run();
	    return;
	}
	try {
	    EXECUTE_ON_REGION.invoke(null, level, pos.immutable(), work);
	} catch (IllegalAccessException e) {
	    throw new IllegalStateException("Cannot access Tessellate compatibility API", e);
	} catch (InvocationTargetException e) {
	    throw new IllegalStateException("Tessellate region dispatch failed", e.getCause());
	}
    }

    public static void runOnMain(Runnable work) {
	if (EXECUTE_ON_MAIN == null) {
	    work.run();
	    return;
	}
	try {
	    EXECUTE_ON_MAIN.invoke(null, work);
	} catch (IllegalAccessException e) {
	    throw new IllegalStateException("Cannot access Tessellate compatibility API", e);
	} catch (InvocationTargetException e) {
	    throw new IllegalStateException("Tessellate main-thread dispatch failed", e.getCause());
	}
    }

    private static Method load() {
	return load("executeOnRegion", ServerLevel.class, BlockPos.class, Runnable.class);
    }

    private static Method load(String name, Class<?>... parameters) {
	try {
	    return Class.forName("org.texboobcat.tessellate.api.TessellateApi").getMethod(name, parameters);
	} catch (ClassNotFoundException | NoSuchMethodException ignored) {
	    return null;
	}
    }
}