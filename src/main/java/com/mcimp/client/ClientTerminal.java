package com.mcimp.client;

import java.io.IOException;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.PrintAboveWriter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.terminal.Terminal.Signal;
import org.jline.terminal.Terminal.SignalHandler;

public class ClientTerminal {
    private Terminal terminal;
    private LineReader reader;
    private PrintAboveWriter writer;

    public ClientTerminal() {
        try {
            terminal = TerminalBuilder.builder().system(true).build();
            reader = LineReaderBuilder.builder().terminal(terminal).build();
            writer = new PrintAboveWriter(reader);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public synchronized void write(int c) {
        writer.write(c);
    }

    public synchronized void write(String str) {
        writer.write(str);
    }

    public synchronized void writeln(String str) {
        writer.write(str + "\n");
    }

    public synchronized void flush() {
        writer.flush();
    }

    public void handle(Signal signal, SignalHandler handler) {
        terminal.handle(signal, handler);
    }

    public String readLine() {
        return readLinePrompt("> ");
    }

    public String readLinePrompt(String prompt) {
        return reader.readLine(prompt);
    }
}
