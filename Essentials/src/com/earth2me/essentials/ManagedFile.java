package com.earth2me.essentials;

import net.ess3.api.IEssentials;
import org.bukkit.Bukkit;

import java.io.*;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tl;


public class ManagedFile {
    private static final int BUFFERSIZE = 1024 * 8;
    private final transient File file;

    public ManagedFile(final String filename, final IEssentials ess) {
        file = new File(ess.getDataFolder(), filename);

        if (file.exists()) {
            try {
                if (checkForVersion(file, ess.getDescription().getVersion()) && !file.delete()) {
                    throw new IOException("Could not delete file " + file.toString());
                }
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        if (!file.exists()) {
            try {
                copyResourceAscii("/" + filename, file);
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, tl("itemsCsvNotLoaded", filename), ex);
            }
        }
    }

    public static void copyResourceAscii(final String resourceName, final File file) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(ManagedFile.class.getResourceAsStream(resourceName))) {
            final MessageDigest digest = getDigest();
            try (DigestOutputStream digestStream = new DigestOutputStream(new FileOutputStream(file), digest)) {
                try (OutputStreamWriter writer = new OutputStreamWriter(digestStream)) {
                    final char[] buffer = new char[BUFFERSIZE];
                    do {
                        final int length = reader.read(buffer);
                        if (length >= 0) {
                            writer.write(buffer, 0, length);
                        } else {
                            break;
                        }
                    } while (true);
                    writer.write("\n");
                    writer.flush();
                    final BigInteger hashInt = new BigInteger(1, digest.digest());
                    digestStream.on(false);
                    digestStream.write('#');
                    digestStream.write(hashInt.toString(16).getBytes());
                }
            }
        }
    }

    public static boolean checkForVersion(final File file, final String version) throws IOException {
        if (file.length() < 33) {
            return false;
        }
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            final byte[] buffer = new byte[(int) file.length()];
            int position = 0;
            do {
                final int length = bis.read(buffer, position, Math.min((int) file.length() - position, BUFFERSIZE));
                if (length < 0) {
                    break;
                }
                position += length;
            } while (position < file.length());
            final ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            if (bais.skip(file.length() - 33) != file.length() - 33) {
                return false;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(bais))) {
                String hash = reader.readLine();
                if (hash != null && hash.matches("#[a-f0-9]{32}")) {
                    hash = hash.substring(1);
                    bais.reset();
                    final String versionline = reader.readLine();
                    if (versionline != null && versionline.matches("#version: .+")) {
                        final String versioncheck = versionline.substring(10);
                        if (!versioncheck.equalsIgnoreCase(version)) {
                            bais.reset();
                            final MessageDigest digest = getDigest();
                            try (DigestInputStream digestStream = new DigestInputStream(bais, digest)) {
                                final byte[] bytes = new byte[(int) file.length() - 33];
                                digestStream.read(bytes);
                                final BigInteger correct = new BigInteger(hash, 16);
                                final BigInteger test = new BigInteger(1, digest.digest());
                                if (correct.equals(test)) {
                                    return true;
                                } else {
                                    Bukkit.getLogger().warning("File " + file.toString() + " has been modified by user and file version differs, please update the file manually.");
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static MessageDigest getDigest() throws IOException {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            throw new IOException(ex);
        }
    }

    public List<String> getLines() {
        try {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                final List<String> lines = new ArrayList<>();
                do {
                    final String line = reader.readLine();
                    if (line == null) {
                        break;
                    } else {
                        lines.add(line);
                    }
                } while (true);
                return lines;
            }
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }
}
