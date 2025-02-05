/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2021 DaPorkchop_
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

package net.daporkchop.lib.primitive.generator.replacer;

import lombok.NonNull;
import net.daporkchop.lib.common.reference.cache.Cached;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.primitive.generator.Primitive;
import net.daporkchop.lib.primitive.generator.TokenReplacer;
import net.daporkchop.lib.primitive.generator.option.ParameterContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author DaPorkchop_
 */
public class GenericHeaderReplacer implements TokenReplacer, Function<List<ParameterContext>, String> {
    private static final Cached<Matcher> GENERIC_HEADER_MATCHER = Cached.regex(Pattern.compile("_G(extends|super)?_"));
    private static final Map<String, Map<List<ParameterContext>, String>> LOOKUP = new HashMap<>();

    static {
        LOOKUP.put("", new ConcurrentHashMap<>());
        LOOKUP.put("extends", new ConcurrentHashMap<>());
        LOOKUP.put("super", new ConcurrentHashMap<>());
    }

    @Override
    public String replace(@NonNull String text, @NonNull List<ParameterContext> params, String pkg) {
        Matcher matcher = GENERIC_HEADER_MATCHER.get().reset(text);
        if (matcher.find()) {
            String header = LOOKUP.get(PorkUtil.fallbackIfNull(matcher.group(1), "")).computeIfAbsent(params, this);
            matcher.hashCode(); //prevent gc
            return header;
        }
        return null;
    }

    @Deprecated
    @Override
    public String apply(@NonNull List<ParameterContext> params) {
        String relation = GENERIC_HEADER_MATCHER.get().group(1);
        return Primitive.getGenericHeader(params, relation == null ? "" : "? " + relation + ' ');
    }
}
