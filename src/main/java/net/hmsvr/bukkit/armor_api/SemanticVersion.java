package net.hmsvr.bukkit.armor_api;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class representing a semantic version.
 * @see <a href="https://semver.org/">Semantic Versioning</a>
 */
public class SemanticVersion {

    private static final Pattern PATTERN = Pattern.compile("^(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?$");

    private final int major, minor, patch;

    /**
     * Initializes a new <code>SemanticVersion</code> object such that
     * it represents the semantic version with the specified major, minor, and patch versions.
     * @param major the major version
     * @param minor the minor version
     * @param patch the patch version
     */
    public SemanticVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    /**
     * Initializes a new <code>SemanticVersion</code> object such that
     * it represents the semantic version represented within the given string.
     * <br>
     * The string format should be that of the regex pattern:<br>
     * <code>^(\d+)(?:\.(\d+))?(?:\.(\d+))?$</code>
     * <br>
     * If the minor and patch versions are missing, they will be interpreted as having a value of zero.
     * For example, <code>1.0</code> would be interpreted as <code>1.0.0</code>.
     * @param version the string
     */
    public SemanticVersion(@NotNull String version) {
        Matcher matcher = PATTERN.matcher(version);
        if (!matcher.matches()) throw new IllegalArgumentException("Invalid semantic version: " + version);
        this.major = Integer.parseInt(matcher.group(1));
        String minor = matcher.group(2);
        String patch = matcher.group(3);
        this.minor = minor != null ? Integer.parseInt(minor) : 0;
        this.patch = patch != null ? Integer.parseInt(patch) : 0;
    }

    /**
     * Gets the major version.
     * @return the major version
     */
    public int getMajor() {
        return major;
    }

    /**
     * Gets the minor version.
     * @return the minor version
     */
    public int getMinor() {
        return minor;
    }

    /**
     * Gets the patch version.
     * @return the patch version
     */
    public int getPatch() {
        return patch;
    }

    /**
     * Tests whether this version is above or equal to the specified version.
     * <br>
     * A version is above another if the precedence is higher.
     * Precedence is determined by the first difference when comparing each identifier from left to right,
     * according to §11 of the Semantic Versioning specification.
     * @see <a href="https://semver.org/#spec-item-11">Semantic Versioning §11</a>
     * @param version the other version
     * @return true if this version is above or equal to the other, otherwise false.
     */
    public boolean above(SemanticVersion version) {
        if (this.major < version.major) return false;
        if (this.minor < version.minor) return false;
        return this.patch >= version.patch;
    }

    /**
     * Tests whether this version is above or equal to the specified version.
     * @see #above(SemanticVersion)
     * @param version the other version
     * @return true if this version is above or equal to the other, otherwise false.
     */
    public boolean above(String version) {
        return above(new SemanticVersion(version));
    }

    /**
     * Returns a string representation of this semantic version
     * by concatenating each identifier with a delimiter of <code>.</code> in order of precedence.
     * <br>
     * For example, a semantic version with
     * a major version of <code>1</code>,
     * a minor version of <code>7</code>,
     * and a patch version of <code>2</code>,
     * would produce the following string: <code>1.7.2</code>
     * @return the string representation of this semantic version.
     */
    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}
