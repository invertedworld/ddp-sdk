namespace DDPEngine;

/// <summary>Thrown when the ddp binary fails (invalid key, parse error, etc.).</summary>
public class EngineError : Exception
{
    /// <summary>Stderr output from the ddp binary.</summary>
    public string Stderr { get; }

    public EngineError(string message, string stderr = "")
        : base(message)
    {
        Stderr = stderr;
    }
}
