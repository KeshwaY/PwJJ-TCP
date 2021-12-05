package me.damianciepiela;

import java.io.IOException;

public interface Closable {
    void close() throws IOException;
}
