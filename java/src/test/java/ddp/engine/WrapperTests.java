package ddp.engine;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WrapperTests {
    private final String root;
    private final String parent;
    private final String inputDir;
    private final String tokenPath;

    WrapperTests() {
        // mvn -f java/pom.xml test runs with cwd = ddp-sdk
        root = Path.of(System.getProperty("user.dir")).normalize().toString();
        parent = Path.of(root).getParent().toString();
        inputDir = Optional.ofNullable(System.getenv("DDP_INPUT_DIR"))
                .orElse(Paths.get(parent, "ddpfiles", "DDP").toString());
        tokenPath = Optional.ofNullable(System.getenv("DDP_TOKEN_FILE"))
                .orElse(Paths.get(root, "test-e2e", "token.txt").toString());
    }

    private String getToken() {
        try {
            return Files.readString(Path.of(tokenPath)).trim();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean inputDirExists() {
        return Files.isDirectory(Path.of(inputDir));
    }

    private Map<String, byte[]> loadDdpFiles() throws Exception {
        Map<String, byte[]> files = new HashMap<>();
        String[] known = {"DDPID", "PQDESCR", "SD", "DDPMS", "DDPMS.DAT", "IMAGE.DAT", "CDTEXT.BIN"};
        for (Path p : Files.list(Path.of(inputDir)).toList()) {
            String name = p.getFileName().toString();
            String key = "SD.SD".equals(name.toUpperCase()) ? "SD" : name.toUpperCase();
            if (Arrays.asList(known).contains(key)) {
                byte[] data = Files.readAllBytes(p);
                if (data.length > 0) {
                    files.put(key, data);
                }
            }
        }
        return files;
    }

    @Test
    void processFromBytes_validKey_writesMetadataAndWavs() throws Exception {
        String token = getToken();
        if (token == null || !inputDirExists()) return;

        Path outDir = Files.createTempDirectory("ddp-test-");
        try {
            JsonNode metadata = DdpEngine.processFromBytes(loadDdpFiles(), outDir.toString(), token);

            assertNotNull(metadata.get("tracks"));
            assertTrue(metadata.get("tracks").isArray());
            int n = metadata.get("tracks").size();
            assertTrue(n > 0);

            assertTrue(Files.exists(outDir.resolve("metadata.json")));
            for (int i = 1; i <= n; i++) {
                Path wavPath = outDir.resolve(String.format("track_%02d.wav", i));
                assertTrue(Files.exists(wavPath));
                byte[] data = Files.readAllBytes(wavPath);
                assertTrue(data.length >= 44);
                assertEquals("RIFF", new String(data, 0, 4));
                assertEquals("WAVE", new String(data, 8, 4));
            }
        } finally {
            deleteRecursive(outDir);
        }
    }

    @Test
    void process_validKey_writesMetadataAndWavs() throws Exception {
        String token = getToken();
        if (token == null || !inputDirExists()) return;

        Path tmp = Files.createTempDirectory("ddp-test-");
        try {
            JsonNode metadata = DdpEngine.process(inputDir, tmp.toString(), token);
            assertNotNull(metadata.get("tracks"));
            assertTrue(metadata.get("tracks").size() > 0);
            assertTrue(Files.exists(tmp.resolve("metadata.json")));
            for (int i = 1; i <= metadata.get("tracks").size(); i++) {
                assertTrue(Files.exists(tmp.resolve(String.format("track_%02d.wav", i))));
            }
        } finally {
            deleteRecursive(tmp);
        }
    }

    @Test
    void process_invalidKey_throwsEngineError() throws Exception {
        if (!inputDirExists()) return;

        Path tmp = Files.createTempDirectory("ddp-test-");
        try {
            assertThrows(EngineError.class, () ->
                    DdpEngine.process(inputDir, tmp.toString(), "invalid-garbage-token"));
        } finally {
            deleteRecursive(tmp);
        }
    }

    private void deleteRecursive(Path p) {
        try {
            if (Files.exists(p)) {
                Files.walk(p).sorted(Comparator.reverseOrder()).forEach(path -> {
                    try { Files.delete(path); } catch (Exception ignored) {}
                });
            }
        } catch (Exception ignored) {}
    }
}
