package org.newtco.obserra.backend.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Version} matching logic according to final implementation rules.
 */
class VersionTest {

    @Test
    @DisplayName("Empty spec matches any version")
    void emptySpecMatchesAll() {
        assertTrue(new Version("5.6.7").matches(new Version("")));
    }

    @Test
    @DisplayName("Exact match on all parts")
    void exactMatch() {
        assertTrue(new Version("1.2.3").matches(new Version("1.2.3")));
        assertFalse(new Version("1.2.3").matches(new Version("1.2.4")));
    }

    @Test
    @DisplayName("Prefix spec without '+' matches only when deeper parts are zero")
    void prefixSpecWithoutPlusRequiresZeros() {
        assertTrue(new Version("3.0.0").matches(new Version("3")), "3.0.0 matches spec '3'");
        assertFalse(new Version("3.0.1").matches(new Version("3")), "3.0.1 does not match spec '3'");
        assertTrue(new Version("3.2.0").matches(new Version("3.2")), "3.2.0 matches spec '3.2'");
        assertFalse(new Version("3.2.1").matches(new Version("3.2")), "3.2.1 does not match spec '3.2'");
    }

    @Test
    @DisplayName("Single-part '+' range semantics on major version")
    void singlePartPlusRange() {
        Version v = new Version("3.2.1");
        assertTrue(v.matches(new Version("1+")), "major >=1");
        assertTrue(v.matches(new Version("3+")), "major >=3");
        assertFalse(v.matches(new Version("4+")), "major <4");
    }

    @Test
    @DisplayName("Multi-part '+' range semantics on last specified part")
    void multiPartPlusRange() {
        Version v = new Version("2.4.5");
        assertTrue(v.matches(new Version("2.3+")), "2.4.x >=2.3");
        assertTrue(v.matches(new Version("2.4+")), "2.4.x >=2.4");
        assertFalse(v.matches(new Version("2.5+")), "2.4.x <2.5");
        assertFalse(v.matches(new Version("3.0+")), "wrong major");
    }

    @Test
    @DisplayName("Spec shorter than actual without '+' mismatches when deeper non-zero parts exist")
    void specShorterThanActualWithoutPlus() {
        Version actual = new Version("1.2.3.4");
        assertFalse(actual.matches(new Version("1.2")));
    }

    @Test
    @DisplayName("Spec shorter than actual with '+' allows any deeper parts")
    void specShorterThanActualWithPlus() {
        Version actual = new Version("1.2.3.4");
        assertTrue(actual.matches(new Version("1.2+")));
    }

    @Test
    @DisplayName("Spec longer than actual without '+' mismatches on missing parts (treated as zero)")
    void specLongerThanActualWithoutPlus() {
        Version actual = new Version("1.2");
        assertFalse(actual.matches(new Version("1.2.0.1")));
    }

    @Test
    @DisplayName("Spec longer than actual with '+' treats missing as >=0 and matches")
    void specLongerThanActualWithPlus() {
        Version actual = new Version("1.2");
        assertTrue(actual.matches(new Version("1.2+")));
    }

    @Test
    @DisplayName("Non-numeric parts parse as zero and follow rules")
    void nonNumericParts() {
        Version actual = new Version("1.2.3");
        assertFalse(actual.matches(new Version("1.x.3")), "x->0 yields 1.0.3 !=1.2.3");
        assertTrue(actual.matches(new Version("1.x+")), "x+->0+ so >=0");
    }

    @Test
    @DisplayName("Invalid spec tokens treated as zero, matching requires equality or '+'/≥ rules")
    void invalidSpecTokens() {
        Version actual = new Version("3.0.1");
        // all invalid => specParts=['...'] -> parsePart('...')==0, plus=false
        assertFalse(actual.matches(new Version("...")));
        // mixed ending '+' only applies to last, but invalid base =>0+
        assertTrue(actual.matches(new Version("3.0+...") ), "'...' parsed as 0+, actual[2]=1>=0");
    }
}
