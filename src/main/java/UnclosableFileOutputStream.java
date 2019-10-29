import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class UnclosableFileOutputStream extends OutputStream {
    private final FileOutputStream outputStream;

    public UnclosableFileOutputStream(FileOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
    }

    @Override
    public void close() throws IOException {
    }

    public void realClose() throws IOException {
        outputStream.close();
    }

    public long getPosition() throws IOException {
        return outputStream.getChannel().position();
    }

}
