/**
 * DDP SDK client — thin wrapper around the ddp binary.
 * API key validation and parsing run in native code.
 */
export declare class EngineError extends Error {
    stderr: string;
    constructor(message: string, stderr?: string);
}
/**
 * Process DDP from in-memory files. Writes to temp dir, invokes ddp binary, returns results.
 * API key validation runs in the native binary.
 */
export declare function processFromBytes(files: Record<string, Buffer | Uint8Array>, apiKey: string): Promise<{
    metadata: object;
    wavs: [string, Buffer][];
}>;
/**
 * Process DDP from a path (directory or ZIP). Invokes ddp binary.
 * API key validation runs in the native binary.
 * Returns the metadata object (metadata.json contents).
 */
export declare function process(inputPath: string, outputPath: string, apiKey: string): Promise<object>;
/**
 * Extract metadata JSON only (no WAV files). Invokes ddp binary.
 * API key validation runs in the native binary.
 * Returns the metadata object. If outputPath is given, also writes metadata.json there.
 */
export declare function processToJson(inputPath: string, apiKey: string, options?: {
    outputPath?: string;
}): Promise<object>;
