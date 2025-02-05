/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.common.util;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.pool.handle.HandledPool;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Some helper methods and values that I use all over the place
 *
 * @author DaPorkchop_
 */
//TODO: clean this up a bit
@UtilityClass
public class PorkUtil {
    public final int TINY_BUFFER_SIZE = 32;
    public final int BUFFER_SIZE = 65536;

    public final HandledPool<byte[]> TINY_BUFFER_POOL = HandledPool.threadLocal(() -> new byte[TINY_BUFFER_SIZE], 4);
    public final HandledPool<ByteBuffer> DIRECT_TINY_BUFFER_POOL = HandledPool.threadLocal(() -> ByteBuffer.allocateDirect(TINY_BUFFER_SIZE), 4);
    public final HandledPool<byte[]> BUFFER_POOL = HandledPool.threadLocal(() -> new byte[BUFFER_SIZE], 4);
    public final HandledPool<ByteBuffer> DIRECT_BUFFER_POOL = HandledPool.threadLocal(() -> ByteBuffer.allocateDirect(BUFFER_SIZE), 4);

    public final HandledPool<StringBuilder> STRINGBUILDER_POOL = HandledPool.threadLocal(StringBuilder::new, 4); //TODO: make this soft

    public final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public final String PORKLIB_VERSION = preventInline("0.5.6-SNAPSHOT"); //TODO: set this dynamically
    public final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    public final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
    public final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public final short[] EMPTY_SHORT_ARRAY = new short[0];
    public final char[] EMPTY_CHAR_ARRAY = new char[0];
    public final int[] EMPTY_INT_ARRAY = new int[0];
    public final long[] EMPTY_LONG_ARRAY = new long[0];
    public final float[] EMPTY_FLOAT_ARRAY = new float[0];
    public final double[] EMPTY_DOUBLE_ARRAY = new double[0];
    public final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    public final boolean NETTY_PRESENT = classExistsWithName("io.netty.util.concurrent.FastThreadLocal");

    /**
     * Casts the given value to the target type without any compile-time warnings or errors.
     * <p>
     * Note that if the cast is impossible, a {@link ClassCastException} will still be thrown at runtime.
     *
     * @param value the value to cast
     * @param <T>   the target type
     * @return the cast value
     */
    @SuppressWarnings("unchecked")
    public static <T> T uncheckedCast(Object value) {
        return (T) value;
    }

    /**
     * Prevents the given value from being inlined at compile-time by {@code javac}.
     * <p>
     * Can be used to keep API backwards-compatibility for {@code static final} constants which might change in the future, or to ensure visibility of reflective
     * modifications to {@code final} member fields.
     * <p>
     * This does not affect the JIT compiler's ability to inline field values at runtime.
     *
     * @param value the value
     * @return the value
     */
    public static <T> T preventInline(T value) {
        return value;
    }

    /**
     * Gets a value, falling back to another value if {@code null}
     *
     * @param value    the value to get
     * @param fallback the fallback value to use
     * @param <T>      the value type
     * @return the value, or the fallback value if the value was {@code null}
     */
    public static <T> T fallbackIfNull(T value, Object fallback) {
        return value != null ? value : uncheckedCast(fallback);
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> classForName(@NonNull String name) {
        try {
            return (Class<T>) Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> classForName(@NonNull String name, ClassLoader loader) {
        try {
            return (Class<T>) Class.forName(name, true, loader);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> uninitializedClassForName(@NonNull String name) {
        try {
            return (Class<T>) Class.forName(name, false, null);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> uninitializedClassForName(@NonNull String name, ClassLoader loader) {
        try {
            return (Class<T>) Class.forName(name, false, loader);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean classExistsWithName(@NonNull String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @SneakyThrows(NoSuchMethodException.class)
    public static Method getMethod(@NonNull Class<?> clazz, @NonNull String name, @NonNull Class<?>... params) {
        try {
            //this will only work for public methods
            return clazz.getMethod(name, params);
        } catch (NoSuchMethodException ignored0) {
            //breadth-first search to find the first class which defines the method
            Set<Class<?>> processed = Collections.newSetFromMap(new IdentityHashMap<>());
            Queue<Class<?>> queue = new ArrayDeque<>();

            queue.add(clazz);

            for (Class<?> curr; (curr = queue.poll()) != null; ) {
                if (!processed.add(curr)) { //this class was already processed
                    continue;
                }

                try {
                    return curr.getDeclaredMethod(name, params);
                } catch (NoSuchMethodException ignored1) {
                    //silently swallow exception
                }

                if (curr.getSuperclass() != null) {
                    queue.add(curr.getSuperclass());
                }
                queue.addAll(Arrays.asList(curr.getInterfaces()));
            }

            //the method isn't defined anywhere
            throw ignored0;
        }
    }

    /**
     * Creates a new instance of {@code sun.misc.SoftCache}.
     * <p>
     * This simply allows not showing compile-time warnings for using internal classes when creating
     * new instances.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @return a new {@code sun.misc.SoftCache}
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public static <K, V> Map<K, V> newSoftCache() {
        try {
            Class<?> clazz = Class.forName("sun.misc.SoftCache");
            return (Map<K, V>) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to find class: sun.misc.SoftCache", e);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e.getCause() == null ? e : e.getCause());
        }
    }

    public static void simpleDisplayImage(@NonNull BufferedImage img) {
        simpleDisplayImage(false, img);
    }

    public static void simpleDisplayImage(boolean block, @NonNull BufferedImage... imgs) {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        for (BufferedImage img : imgs) {
            frame.getContentPane().add(new JLabel(new ImageIcon(img)));
        }
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        if (block) {
            CompletableFuture future = new CompletableFuture();
            frame.addWindowListener(new WindowAdapter() {
                @Override
                @SuppressWarnings("unchecked")
                public void windowClosed(WindowEvent e) {
                    future.complete(null);
                }
            });
            try {
                future.get();
            } catch (InterruptedException
                    | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static String className(Object obj) {
        return obj == null ? "null" : obj.getClass().getTypeName();
    }

    public void unsafe_forceGC() {
        Object obj = new Object();
        long oldMem = Runtime.getRuntime().freeMemory();
        obj = null;
        do {
            System.gc();
        } while (Runtime.getRuntime().freeMemory() <= oldMem);
    }

    public static Object getNull() {
        return null;
    }

    @SneakyThrows({ IllegalAccessException.class, InstantiationException.class, InvocationTargetException.class, NoSuchMethodException.class })
    public static <T> T newInstance(@NonNull Class<T> clazz) {
        Constructor<T> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }
}
