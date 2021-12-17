package me.damianciepiela;

import java.io.IOException;
import java.sql.SQLException;

public interface Closable {
    void close() throws IOException, SQLException;
}
