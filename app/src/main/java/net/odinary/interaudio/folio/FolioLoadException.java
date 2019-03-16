package net.odinary.interaudio.folio;

public class FolioLoadException extends Exception
{
    public FolioLoadException(String s) {
        throw new RuntimeException(s);
    }

    public FolioLoadException(String message, Throwable cause) {
        throw new RuntimeException(message, cause);
    }

    public FolioLoadException(Throwable cause) {
        throw new RuntimeException(cause);
    }
}