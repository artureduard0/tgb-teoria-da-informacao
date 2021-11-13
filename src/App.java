import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class App {
    public static void main(String[] args) throws Exception {
        try {
            boolean executar = true;

            while (executar) {
                Object[] opcoes = { "Codificar", "Decodificar" };

                int opcao = JOptionPane.showOptionDialog(null, "Escolha o que executar: ",
                        "Trabalho GB - Encoder/Decoder", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                        null, opcoes, opcoes[0]);

                if (opcao != -1) {
                    JFileChooser chooser = new JFileChooser();
                    File path = new File("./arquivos");
                    chooser.setCurrentDirectory(path);
                    chooser.setMultiSelectionEnabled(false);
                    int retorno = chooser.showOpenDialog(null);

                    if (retorno != 1) {
                        File arquivo = chooser.getSelectedFile();
                        Path caminho = arquivo.toPath();
                        String nomeArquivo = caminho.toString().replaceFirst("[.][^.]+$", "");

                        byte[] data = read(caminho);

                        /**
                         * (0) Codificar e (1) Decodificar
                         */
                        if (opcao == 0) {
                        } else {

                        }
                    } else {
                        executar = false;
                    }
                } else {
                    executar = false;
                }
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            System.out.println(exceptionAsString);

            JOptionPane.showMessageDialog(null, "Ah não, uma exceção aconteceu! Saindo...", "Erro fatal!",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void writeBits(String filename, byte[] bytes) throws IOException {
        File outFile = new File(filename);
        FileOutputStream fos = new FileOutputStream(outFile);
        fos.write(bytes);
        fos.close();
    }

    public static byte[] read(Path path) throws IOException {
        return Files.readAllBytes(path);
    }
}
