package com.mcimp.client;

import java.io.IOException;

import org.jline.utils.InfoCmp;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.PrintAboveWriter;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Attributes;
import org.jline.terminal.Terminal;
import org.jline.terminal.Terminal.Signal;
import org.jline.terminal.Terminal.SignalHandler;
import org.jline.terminal.TerminalBuilder;
import org.jline.terminal.Attributes.LocalFlag;

public class ClientTerminal implements AutoCloseable {
    private Terminal terminal;
    private LineReader reader;
    private PrintAboveWriter writer;

    public ClientTerminal() {
        try {
            terminal = TerminalBuilder.builder().system(true).build();
            var originalAttributes = terminal.getAttributes();
            var rawAttributes = new Attributes(originalAttributes);
            rawAttributes.setLocalFlag(LocalFlag.ICANON, false);
            rawAttributes.setLocalFlag(LocalFlag.ECHO, false);
            terminal.setAttributes(rawAttributes);
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

    public void clearPrevLine() {
        terminal.puts(InfoCmp.Capability.cursor_up);
        terminal.puts(InfoCmp.Capability.clr_eos);
        terminal.flush();
    }


    public String readLine() throws UserInterruptException, EndOfFileException {
        return readLinePrompt("> ");
    }

    public String readLine(Character mask) throws UserInterruptException, EndOfFileException {
        return readLinePrompt("> ", mask);
    }

    public String readLine(String prompt) throws UserInterruptException, EndOfFileException {
        return readLinePrompt(prompt);
    }

    public String readLine(String prompt, Character mask) throws UserInterruptException, EndOfFileException {
        return readLinePrompt(prompt, mask);
    }

    public String readLinePrompt(String prompt) throws UserInterruptException, EndOfFileException {
        return reader.readLine(prompt);
    }

    public String readLinePrompt(String prompt, Character mask) throws UserInterruptException, EndOfFileException {
        return reader.readLine(prompt, mask);
    }

    @Override
    public void close() throws IOException {
        writer.close();
        terminal.close();
    }
}
