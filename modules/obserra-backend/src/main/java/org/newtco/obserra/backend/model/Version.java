package org.newtco.obserra.backend.model;

import jakarta.annotation.Nonnull;

/**
 * A semantic version wrapper that supports custom matching rules:
 * <ul>
 *   <li>Empty spec: matches any actual version.</li>
 *   <li>Exact or prefix match: a spec part without '+' must equal the corresponding actual part.</li>
 *   <li>'+' suffix (">=") match: a spec part ending with '+' allows the actual part to be
 *       greater than or equal to the spec number.</li>
 *   <li>Spec normalization: if the spec has fewer parts than the actual version,
 *       missing spec parts default to zero or to &#39;0+&#39; if the last provided spec part
 *       included a '+'.</li>
 *   <li>Actual normalization: missing actual parts default to zero when spec is longer.</li>
 * </ul>
 *
 * <p>Examples:</p>
 * <pre>
 *   new Version("3.0.1").matches(new Version("3"))    // false (prefix match)
 *   new Version("3.2.1").matches(new Version("3.2"))  // false (prefix match)
 *   new Version("3.2.1").matches(new Version("1+"))   // true (major &gt;= 1)
 *   new Version("2.4.5").matches(new Version("2.3+")) // true (major=2 && minor&gt;=3)
 *   new Version("3.0.1").matches(new Version("3.0.0.5+")) // false (fourth part 1 &lt;= 5)
 * </pre>
 */
public record Version(@Nonnull String raw) {

    /**
     * Determine if this version satisfies the given specification.
     *
     * <p>Algorithm summary:</p>
     * <ol>
     *   <li>If the spec string is empty, return true (no constraint).</li>
     *   <li>Split spec and actual version by '.'.</li>
     *   <li>Detect if the last spec part ends with '+'.</li>
     *   <li>Iterate through the maximum part count of spec and actual:</n     *     <ul>
     *       <li>For spec indices beyond spec length, use '0+' if last spec had '+', else '0'.</li>
     *       <li>For actual indices beyond actual length, use '0'.</li>
     *       <li>Strip '+' from spec part if present, parse both to integers.</li>
     *       <li>If '+' was present, require actualPart &gt;= specPart; otherwise require equality.</li>
     *     </ul>
     *   </li>
     *   <li>Return true only if all part checks pass.</li>
     * </ol>
     *
     * @param spec the version specification to match against
     * @return true if this version satisfies the spec, false otherwise
     */
    public boolean matches(Version spec) {
        String specRaw = spec.raw();
        if (specRaw.isEmpty()) {
            return true; // no constraint
        }

        String[] specTokens   = specRaw.split("\\.");
        String[] actualTokens = raw.split("\\.");
        boolean   carryPlus   = specTokens.length > 0 && specTokens[specTokens.length - 1].endsWith("+");
        int       maxLen      = Math.max(specTokens.length, actualTokens.length);

        for (int i = 0; i < maxLen; i++) {
            // Determine spec token, normalizing beyond end
            String token = i < specTokens.length
                           ? specTokens[i]
                           : (carryPlus ? "0+" : "0");

            boolean plus     = token.endsWith("+");
            String  base     = plus ? token.substring(0, token.length() - 1) : token;
            int     specVal  = parsePart(base);
            int     actualVal = i < actualTokens.length
                                ? parsePart(actualTokens[i])
                                : 0;

            if (plus) {
                if (actualVal < specVal) {
                    return false;
                }
            } else {
                if (actualVal != specVal) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Safely parse an integer part, defaulting to zero on failure.
     */
    private static int parsePart(String p) {
        try {
            return Integer.parseInt(p);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
