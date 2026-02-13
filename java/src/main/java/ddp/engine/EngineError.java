package ddp.engine;

/**
 * Thrown when the ddp binary fails (invalid key, parse error, etc.).
 */
public class EngineError extends RuntimeException {
    private final String stderr;

    public EngineError(String message) {
        this(message, "");
    }

    public EngineError(String message, String stderr) {
        super(message);
        this.stderr = stderr != null ? stderr : "";
    }

    public String getStderr() {
        return stderr;
    }
}
