package org.newtco.bootmonitoring;

public interface ServiceIdGenerator {
    /**
     * Generate a new service ID.
     *
     * @return The generated service ID.
     */
    default String generate() {
        var alphabet = alphabet();
        return new java.security.SecureRandom().ints(7, 0, alphabet.length())
            .map(alphabet::charAt)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }

    /**
     * Get the alphabet to use for generating service IDs.
     *
     * @return The alphabet to use for generating service IDs.
     */
    default String alphabet() {
        return "abcdefghijklmnopqrstuvwxyz0123456789";
    }
}
