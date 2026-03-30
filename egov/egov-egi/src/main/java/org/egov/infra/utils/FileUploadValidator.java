package org.egov.infra.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Server-side file upload validator — fixes CWE-434 (Unrestricted File Upload).
 *
 * Addresses the specific attack vectors in your controller:
 *
 *  ATTACK 1 — Burp Suite extension swap:
 *    Attacker uploads evil.bat renamed to evil.png in the HTTP request.
 *    Fixed by: magic byte check + ImageIO decode check.
 *
 *  ATTACK 2 — Content-Type spoofing:
 *    Your original code passes logo.getContentType() to fileStoreService.store().
 *    Burp Suite freely sets Content-Type: image/png for a .bat file.
 *    Fixed by: getSafeContentType() derives MIME from the validated extension,
 *    never from the HTTP header.
 *
 *  ATTACK 3 — Filename injection / path traversal / double extension:
 *    logo.getOriginalFilename() is attacker-controlled.
 *    "shell.jsp.png" or "../../etc/cron.d/evil.png" are valid inputs.
 *    Fixed by: sanitizeFileName() strips paths, blocks double dangerous
 *    extensions, and prepends UUID for safe unique storage names.
 */
public class FileUploadValidator {

    // ------------------------------------------------------------------
    // Whitelists
    // ------------------------------------------------------------------

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif"
    ));

    /** Maps validated extension to safe MIME type. Never trust the HTTP header. */
    private static final Map<String, String> EXTENSION_TO_MIME = new HashMap<>();
    static {
        EXTENSION_TO_MIME.put("jpg",  "image/jpeg");
        EXTENSION_TO_MIME.put("jpeg", "image/jpeg");
        EXTENSION_TO_MIME.put("png",  "image/png");
        EXTENSION_TO_MIME.put("gif",  "image/gif");
    }

    /** Extensions that must NEVER appear anywhere in the filename. */
    private static final String[] DANGEROUS_EXTENSIONS = {
            ".php", ".jsp", ".jspx", ".jsw", ".jsv",
            ".bat", ".cmd", ".sh", ".py", ".rb", ".pl",
            ".exe", ".dll", ".war", ".jar", ".class",
            ".asp", ".aspx", ".cfm", ".cgi", ".htaccess",
            ".svg"  // SVG can contain embedded JS
    };

    /** Max file size: 2 MB */
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024L;

    // ------------------------------------------------------------------
    // Magic byte signatures
    // ------------------------------------------------------------------

    private static final byte[] JPEG_MAGIC  = {(byte)0xFF, (byte)0xD8, (byte)0xFF};
    private static final byte[] PNG_MAGIC   = {(byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] GIF87_MAGIC = {0x47, 0x49, 0x46, 0x38, 0x37, 0x61};
    private static final byte[] GIF89_MAGIC = {0x47, 0x49, 0x46, 0x38, 0x39, 0x61};

    // ------------------------------------------------------------------
    // Primary validation — call BEFORE fileStoreService.store()
    // ------------------------------------------------------------------

    /**
     * 5-layer validation. Throws IllegalArgumentException on failure.
     * Throws IOException if file bytes cannot be read.
     */
    public static void validateImageFile(MultipartFile file) throws IOException {

        // Layer 0: Size
        if (file.getSize() > MAX_FILE_SIZE)
            throw new IllegalArgumentException("File size exceeds the 2 MB limit.");

        // Layer 1: Filename has extension
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.trim().isEmpty() || !originalName.contains("."))
            throw new IllegalArgumentException("File must have a valid name and extension.");

        String lowerName = originalName.toLowerCase().trim();

        // Layer 2: Block dangerous extensions ANYWHERE in the filename
        //          Catches "shell.jsp.png", "evil.bat.jpg", "../passwd" etc.
        for (String dangerous : DANGEROUS_EXTENSIONS) {
            if (lowerName.contains(dangerous))
                throw new IllegalArgumentException(
                    "Filename contains a disallowed pattern. Upload rejected.");
        }

        // Layer 3: Extension whitelist
        String ext = lowerName.substring(lowerName.lastIndexOf('.') + 1);
        if (!ALLOWED_EXTENSIONS.contains(ext))
            throw new IllegalArgumentException(
                "Invalid file type. Only JPG, JPEG, PNG, and GIF are allowed.");

        // Layer 4: Magic bytes — stops a renamed .bat/.exe file cold
        byte[] header = readFirstBytes(file.getInputStream(), 8);
        if (!hasValidImageSignature(header))
            throw new IllegalArgumentException(
                "File content does not match a valid image. Upload rejected.");

        // Layer 5: ImageIO decode — a script renamed to .png cannot be decoded
        try (InputStream is = file.getInputStream()) {
            BufferedImage img = ImageIO.read(is);
            if (img == null)
                throw new IllegalArgumentException(
                    "File could not be decoded as a valid image. Upload rejected.");
        }
    }

    // ------------------------------------------------------------------
    // Used in controller when calling fileStoreService.store()
    // ------------------------------------------------------------------

    /**
     * Returns MIME type derived from the file EXTENSION — NOT from the HTTP
     * Content-Type header (which Burp Suite can freely spoof).
     *
     * Use this instead of multipartFile.getContentType().
     */
    public static String getSafeContentType(String originalFilename) {
        if (originalFilename == null) return "application/octet-stream";
        String ext = originalFilename.toLowerCase().trim();
        ext = ext.contains(".") ? ext.substring(ext.lastIndexOf('.') + 1) : "";
        return EXTENSION_TO_MIME.getOrDefault(ext, "application/octet-stream");
    }

    /**
     * Returns a sanitized filename safe for disk storage:
     *   - Strips path separators (prevents path traversal attacks)
     *   - Keeps only safe characters
     *   - Prepends UUID to prevent collisions and filename guessing
     *
     * Use this instead of multipartFile.getOriginalFilename().
     *
     * Examples:
     *   "../../etc/evil.png"  →  "a3f2c1d0-xxxx.png"
     *   "Company Logo.PNG"    →  "b9e1f2a3-xxxx.png"
     */
    public static String sanitizeFileName(String originalFilename) {
        if (originalFilename == null || originalFilename.trim().isEmpty())
            return UUID.randomUUID().toString() + ".jpg";

        // Strip any directory traversal components
        String base = originalFilename
                .replaceAll(".*[/\\\\]", "")          // strip path separators
                .replaceAll("[^a-zA-Z0-9._-]", "_")   // allow only safe characters
                .trim();

        String ext = base.contains(".")
                ? base.substring(base.lastIndexOf('.') + 1).toLowerCase()
                : "jpg";

        // UUID prefix ensures uniqueness and prevents filename-based guessing
        return UUID.randomUUID().toString() + "." + ext;
    }

    // ------------------------------------------------------------------
    // Private helpers
    // ------------------------------------------------------------------

    private static byte[] readFirstBytes(InputStream is, int length) throws IOException {
        byte[] buffer = new byte[length];
        int read = is.read(buffer, 0, length);
        if (read < 3)
            throw new IllegalArgumentException("File is too small to be a valid image.");
        return buffer;
    }

    private static boolean hasValidImageSignature(byte[] bytes) {
        return startsWith(bytes, JPEG_MAGIC)
            || startsWith(bytes, PNG_MAGIC)
            || startsWith(bytes, GIF87_MAGIC)
            || startsWith(bytes, GIF89_MAGIC);
    }

    private static boolean startsWith(byte[] data, byte[] magic) {
        if (data.length < magic.length) return false;
        for (int i = 0; i < magic.length; i++)
            if (data[i] != magic[i]) return false;
        return true;
    }
}