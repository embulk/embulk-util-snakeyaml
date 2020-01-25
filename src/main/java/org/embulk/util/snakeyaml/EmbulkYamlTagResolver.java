/*
 * Copyright 2020 The Embulk project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.embulk.util.snakeyaml;

import java.util.regex.Pattern;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.resolver.Resolver;

/**
 * Embulk's customized SnakeYaml {@link org.yaml.snakeyaml.resolver.Resolver} which converts a node (scalar, sequence, map, or !!tag with them) to a tag (INT, FLOAT, STR, SEQ, MAP, ...).
 *
 * <p>For example, it converts {@code "123"} (scalar) to {@code 123} (INT), or {@code "true"} (scalar) to {@code true} (BOOL).
 * This is called by SnakeYaml's {@link org.yaml.snakeyaml.composer.Composer} which converts parser events into an object.
 *
 * <p>Note that <a href="https://github.com/FasterXML/jackson-dataformat-yaml">{@code jackson-dataformat-yaml}</a>
 * doesn't use this because it traverses parser events without using {@link org.yaml.snakeyaml.composer.Composer}.
 */
public final class EmbulkYamlTagResolver extends Resolver {
    /**
     * Adds an implicit resolver.
     *
     * <p>This method is called by constructor through {@code addImplicitResolvers} to setup default implicit resolvers.
     */
    @Override
    public void addImplicitResolver(final Tag tag, final Pattern regexp, final String first) {
        if (tag.equals(Tag.FLOAT)) {
            super.addImplicitResolver(Tag.FLOAT, FLOAT_EXCEPTING_ZERO_START, "-+0123456789.");
        } else if (tag.equals(Tag.BOOL)) {
            // use stricter rule (reject 'On', 'Off', 'Yes', 'No')
            super.addImplicitResolver(Tag.BOOL, Pattern.compile("^(?:[Tt]rue|[Ff]alse)$"), "TtFf");
        } else if (tag.equals(Tag.TIMESTAMP)) {
            // This solves some unexpected behavior that snakeyaml
            // deserializes "2015-01-01 00:00:00" to java.util.Date
            // but jackson serializes java.util.Date to an integer.
            return;
        } else {
            super.addImplicitResolver(tag, regexp, first);
        }
    }

    /**
     * Resolves.
     */
    @Override
    public Tag resolve(final NodeId kind, final String value, final boolean implicit) {
        return super.resolve(kind, value, implicit);  // checks implicit resolvers
    }

    private static final Pattern FLOAT_EXCEPTING_ZERO_START = Pattern.compile(
            "^([-+]?(\\.[0-9]+|[1-9][0-9_]*(\\.[0-9_]*)?)([eE][-+]?[0-9]+)?|[-+]?[0-9][0-9_]*(?::[0-5]?[0-9])+\\.[0-9_]*|[-+]?\\.(?:inf|Inf|INF)|\\.(?:nan|NaN|NAN))$");
}
