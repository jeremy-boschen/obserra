package org.newtco.obserra.backend.util;

import java.io.Serial;

import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A YAML-style ToStringStyle for Apache Commons Lang 3 that:
 *  - omits class names & identity hashes
 *  - emits one field per line as "key: value"
 *  - indents nested toString() calls automatically by 2 spaces
 *
 * Use via ReflectionToStringBuilder:
 *   ReflectionToStringBuilder.toString(this, YamlToStringStyle.INSTANCE)
 */
public final class YamlToStringStyle extends ToStringStyle {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Singleton instance. */
    public static final YamlToStringStyle INSTANCE = new YamlToStringStyle();

    private YamlToStringStyle() {
        super();

        // Don’t print the class name or identity-hash
        setUseClassName(false);
        setUseIdentityHashCode(false);

        // No surrounding braces/text
        setContentStart("");
        setContentEnd("");

        // One field per line
        setFieldSeparator(System.lineSeparator());
        setFieldSeparatorAtStart(false);

        // "key: value"
        setFieldNameValueSeparator(": ");

        // Nested indentation step (applies when nested objects produce multi-line toString)
//        setIndentSize(2);

        // Arrays in YAML-list style or JSON-style? JSON-style here; tweak if you prefer "- elem"
        setArrayStart("[");
        setArrayEnd("]");
        setArraySeparator(", ");

        // How to show nulls
        setNullText("null");
    }

    @Override
    public void appendStart(final StringBuffer buffer, final Object object) {
        // no-op: we don't want any leading marker
    }

    @Override
    protected void appendClassName(final StringBuffer buffer, final Object object) {
        // no-op
    }

    @Override
    protected void appendIdentityHashCode(final StringBuffer buffer, final Object object) {
        // no-op
    }
}
