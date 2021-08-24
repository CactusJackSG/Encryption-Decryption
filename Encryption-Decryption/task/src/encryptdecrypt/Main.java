package encryptdecrypt;

import java.io.*;
import java.util.HashMap;

enum DecEncType {
    SHIFT,
    UNICODE;
}

interface DecEnc {
    public String encrypt(String s);
    public String decrypt(String s);
}

class UnicodeDecEnc implements DecEnc{

    int key;

    public UnicodeDecEnc (int key) {
        this.key = key;
    }

    @Override
    public String encrypt(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            sb.append((char) (c + key));
        }
        return sb.toString();
    }

    @Override
    public String decrypt(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            sb.append((char) (c - key));
        }
        return sb.toString();
    }
}

class ShiftDecEnc implements DecEnc {

    int key;

    public ShiftDecEnc (int key) {
        this.key = key;
    }

    @Override
    public String encrypt(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c >= 65 && c <= 90) {
                if ((c + key) > 90) {
                    sb.append((char) (65 + (c + key - 1) - 90 ));
                } else {
                    sb.append((char) (c + key));
                }
            } else if (c >= 97 && c <= 122){
                if ((c + key) > 122) {
                    sb.append((char) (97 + (c + key - 1) - 122 ));
                } else {
                    sb.append((char) (c + key));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Override
    public String decrypt(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c >= 65 && c <= 90) {
                if ((c - key) < 65) {
                    sb.append((char) (90 - (65 - (c - key + 1))));
                } else {
                    sb.append((char) (c - key));
                }
            } else if (c >= 97 && c <= 122){
                if ((c - key) < 97) {
                    sb.append((char) (122 - (97 - (c - key + 1))));
                } else {
                    sb.append((char) (c - key));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}

class DecEncFabric {
    public static DecEnc getDecEnc(DecEncType type, int key) {
        switch (type) {
            case SHIFT:
                return new ShiftDecEnc(key);
            case UNICODE:
                return new UnicodeDecEnc(key);
            default:
                return null;
        }
    }
}

public class Main {

    public static void main(String[] args) throws IOException {
        String mode = "enc";
        DecEncType decEncType = DecEncType.SHIFT;
        String s = "";
        int key = 0;
        String fileIn = "";
        String fileOut = "";
        HashMap <String, String> input = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            input.put(args[i], args[i + 1]);
        }
        for (String mapKey : input.keySet()) {
            if (mapKey.equals("-mode")) {
                mode = input.get(mapKey);
                continue;
            }
            if (mapKey.equals("-key")) {
                key = Integer.parseInt(input.get(mapKey));
                continue;
            }
            if (mapKey.equals("-data")) {
                s = input.get(mapKey);
                continue;
            }
            if (mapKey.equals("-out")) {
                fileOut = input.get(mapKey);
                continue;
            }
            if (mapKey.equals("-in")) {
                fileIn = input.get(mapKey);
                continue;
            }
            if (mapKey.equals("-alg")) {
                decEncType = input.get("-alg").equals("unicode") ? DecEncType.UNICODE : DecEncType.SHIFT;
            }
        }

        PrintStream printStream = new PrintStream(System.out);

        if (!fileIn.equals("")) {
            printStream = new PrintStream(fileOut);

        }

        if (s.equals("") && !fileIn.equals("")) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileIn));
            try {
                s = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                bufferedReader.close();
            }
        }
        DecEnc decEnc = DecEncFabric.getDecEnc(decEncType, key);

        if (mode.equals("enc")) {
            printStream.println(decEnc.encrypt(s));
        } else {
            printStream.println(decEnc.decrypt(s));
        }
        printStream.close();

    }
}
