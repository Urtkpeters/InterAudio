package net.odinary.interaudio;

public class PackageLoadException extends Exception
{
    public PackageLoadException(String s) {
        throw new RuntimeException(s);
    }

    public PackageLoadException(String message, Throwable cause) {
        throw new RuntimeException(message, cause);
    }

    public PackageLoadException(Throwable cause) {
        throw new RuntimeException(cause);
    }
}