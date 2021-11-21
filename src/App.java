import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class App {
    private static int NUM_ROUNDS = 4;

    public static void main(String[] args) throws Exception {
        try {
            boolean executar = true;

            while (executar) {
                Object[] opcoes = { "Criptografar", "Descriptografar" };

                int opcao = JOptionPane.showOptionDialog(null, "Escolha o que executar: ",
                        "Trabalho GB - Cifrador simétrico de bloco", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE, null, opcoes, opcoes[0]);

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

                        // * Key scheduling

                        String key = "";

                        while (key.length() != 4) {
                            key = JOptionPane.showInputDialog(null, "Informe a chave de ciframento de 4 bytes: ",
                                    "Trabalho GB - Cifrador simétrico de bloco", JOptionPane.QUESTION_MESSAGE);
                        }

                        String[] strings = keyScheduling(key);

                        ArrayList<byte[]> blocos = new ArrayList<>();
                        int indexBlocos = 0;
                        byte[] bloco = new byte[6];

                        for (int i = 0; i < data.length; i++) {
                            // se atingir os 6 bytes do bloco ou não houver blocos o suficiente para fechar
                            // os 6, adicionar.
                            if (indexBlocos == 6 || i + 1 == data.length) {
                                indexBlocos = 0;
                                blocos.add(bloco);
                                bloco = new byte[6];
                            }

                            bloco[indexBlocos++] = data[i];
                        }

                        for (int i = 0; i < blocos.size(); i++) {
                            for (int j = 0; j < blocos.get(i).length; j++) {
                                // System.out.println((int) blocos.get(i)[j]);
                            }
                        }

                        if (opcao == 0) {
                            encrypt(blocos);
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

    public static String toBinary(int x, int len) {
        if (len > 0) {
            return String.format("%" + len + "s", Integer.toBinaryString(x)).replaceAll(" ", "0");
        }
        return null;
    }

    public static String[] split(String s) {
        String[] strings = s.split("(?<=\\G.{8})");
        return strings;
    }

    private static String[] keyScheduling(String key) {
        byte[] keyBytes = key.getBytes();
        // System.out.println("-----------------------------------");
        // System.out.println("Bytes: " + Arrays.toString(keyBytes));

        String keyBinary = "";
        for (int i = 0; i < keyBytes.length; i++) {
            String s = toBinary(keyBytes[i], 8);
            // System.out.println("Byte posição " + i + " em binário: " + s);
            keyBinary += s;
        }
        // System.out.println("\nChave em binário: " + keyBinary);

        int[] table = { 32, 28, 24, 20, 16, 12, 8, 4, 29, 25, 21, 17, 13, 9, 5, 1, 31, 27, 23, 19, 15, 11, 7, 3, 30, 26,
                22, 18, 14, 10, 6, 2 };
        // int[] tableInverted = {16, 32, 24, 8, 15, 31, 23, 7, 14, 30, 22, 6, 13, 29,
        // 21, 5, 12, 28, 20, 4, 11, 27, 19, 3, 10, 26, 18, 2, 9, 25, 17, 1};
        String permutedKey = permutation(keyBinary, table, false, 0);

        String[] strings = split(permutedKey);
        // System.out.println("-----------------------------------");
        // System.out.println("Chaves em 8 bits: " + Arrays.toString(strings));

        int[] values = new int[4];
        for (int i = 0; i < values.length; i++) {
            values[i] = Integer.parseInt(strings[i], 2);
        }
        // System.out.println("Chaves em inteiros: " + Arrays.toString(values));

        strings = rightShift(values, 8);

        int[] expansionTable = { 8, 1, 5, 2, 4, 6, 5, 2, 7, 1, 8, 7, 6, 3, 3, 4 };
        // System.out.println("-----------------------------------");
        for (int i = 0; i < strings.length; i++) {
            // System.out.println("Subchave " + i + ", expansão de 8 > 16 bits");
            strings[i] = permutation(strings[i], expansionTable, true, 16);
        }

        // System.out.println("-----------------------------------");
        for (int i = 0; i < values.length; i++) {
            values[i] = Integer.parseInt(strings[i], 2);
        }
        // System.out.println("Subchaves em inteiros: " + Arrays.toString(values));

        strings = rightShift(values, 16);

        int[] expansionTable2 = { 9, 15, 7, 3, 5, 11, 13, 1, 2, 4, 9, 10, 12, 16, 5, 3, 2, 10, 7, 11, 8, 6, 14, 9 };
        // System.out.println("-----------------------------------");
        for (int i = 0; i < strings.length; i++) {
            // System.out.println("Subchave " + i + ", expansão de 16 > 24 bits");
            strings[i] = permutation(strings[i], expansionTable2, true, 24);
        }

        return strings;
    }

    private static String permutation(String key, int[] table, boolean isExpansion, int size) {

        StringBuilder s = new StringBuilder(key);
        StringBuilder sPermuted;

        if (isExpansion) {
            sPermuted = new StringBuilder();
            sPermuted.setLength(size);
            for (int i = 0; i < table.length; i++) {
                char character = s.charAt(table[i] - 1);
                sPermuted.setCharAt(i, character);
            }
        }

        else {
            sPermuted = new StringBuilder(key);
            for (int i = 0; i < s.length(); i++) {
                int newPosition = table[i] - 1;
                char value = s.charAt(i);
                sPermuted.setCharAt(newPosition, value);
            }
        }

        // System.out.println("Chave permutada:  " + sPermuted.toString());
        return sPermuted.toString();
    }

    private static String[] rightShift(int[] values, int size) {
        String[] strings = new String[4];
        // System.out.println("-----------------------------------");
        for (int i = 0; i < values.length; i++) {
            int n = values[i];
            n = (n >>> 1) | (n << (size - 1));
            String s = toBinary(n, size);
            s = s.substring(s.length() - size);
            strings[i] = s;
            // System.out.println("Subchave " + i + " após >>> " + strings[i]);
        }
        return strings;
    }

    private static void encrypt(ArrayList<byte[]> blocos) {
        // * Permutação inicial
        int[] t1 = {43, 37, 31, 25, 19, 13, 7,  1,
                    47, 41, 35, 29, 23, 17, 11, 5,
                    45, 39, 33, 27, 21, 15, 9,  3,
                    48, 42, 36, 30, 24, 18, 12, 6,
                    44, 38, 32, 26, 20, 14, 8,  2,
                    46, 40, 34, 28, 22, 16, 10, 4,
        };

        ArrayList<String> blocosPermutados = new ArrayList<>();

        for (int i = 0; i < blocos.size(); i++) {
            byte[] bloco = blocos.get(i);
            String blockBinary = "";
            for (int j = 0; j < bloco.length; j++) {
                String s = toBinary(bloco[j], 8);
                blockBinary += s;
            }
            blocosPermutados.add(permutation(blockBinary, t1, false, 48));
            // System.out.println("Bloco " + i + " em binário: " + blockBinary);
            // System.out.println("Bloco " + i + " permutado em binário: " + blocosPermutados.get(i));
        }
        
        


        boolean inverter = false;
        int roundAtual = 0;

        ArrayList<byte[]> blocosEncriptados = new ArrayList<>();
        for (int i = 0; i < blocos.size(); i++) {
            byte[] left = new byte[3];
            System.arraycopy(blocos.get(i), 0, left, 0, 3);

            byte[] right = new byte[3];
            System.arraycopy(blocos.get(i), 3, right, 0, 3);

            inverter = false;

            while (roundAtual < NUM_ROUNDS) {
                if (inverter) {
                    left = right;
                    right = left;
                }

                blocosEncriptados.add(round(left, right));
                inverter = !inverter;
                roundAtual++;
            }
        }
    }

    private static byte[] round(byte[] left, byte[] right) {
        byte[] retorno = new byte[6];

        return retorno;
    }
}
