import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Game {
    static String convert(byte[] arr) {
        StringBuilder resultRandom = new StringBuilder();
        for (byte i : arr) {
            int decimal = (int) i & 0xff;
            String hex = Integer.toHexString(decimal);
            if (hex.length() % 2 == 1)
                hex = "0" + hex;
            resultRandom.append(hex);
        }
        return resultRandom.toString();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {
        Scanner sc = new Scanner(System.in);
        ArrayList<String> gameWords = new ArrayList<>(Arrays.asList(sc.nextLine().split(" ")));

        if (gameWords.stream().distinct().count() != gameWords.size()) {
            System.out.println("\nIncorrect data entry. Duplicate words.");
            return;
        }

        if (gameWords.size() < 3 || gameWords.size() % 2 == 0) {
            System.out.println("\nIncorrect data entry. The word count is less than 3 or even.");
            return;
        }

        SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        byte[] bytes = secureRandom.generateSeed(16);

        int computerWord = (int) Math.floor(Math.random() * gameWords.size());
        String key = convert(bytes);
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        sha256_HMAC.init(new SecretKeySpec(key.getBytes(), "HmacSHA256"));
        byte[] hmac = sha256_HMAC.doFinal(gameWords.get(computerWord).getBytes());
        System.out.println("\nHMAC: " + convert(hmac));

        int playerWord = 0;
        while (true) {
            System.out.println("\nAvailable moves:");
            for (int i = 0; i < gameWords.size(); i++)
                System.out.println(i + 1 + " - " + gameWords.get(i));
            System.out.println(0 + " - Exit");

            System.out.print("\nChoose a word: ");
            if (!sc.hasNextInt()) {
                System.out.println("\nIncorrect data entry. A positive integer from available moves is expected.");
                sc.nextLine();
            } else {
                playerWord = sc.nextInt();
                if (playerWord > gameWords.size() || playerWord < 0)
                    System.out.println("\nIncorrect data entry. A positive integer from available moves is expected.");
                else if (playerWord == 0) {
                    System.out.println("Goodbye!");
                    return;
                } else break;
            }
        }

        System.out.println("\nPlayer choice: " + gameWords.get(playerWord - 1));
        System.out.println("\nComputer choice: " + gameWords.get(computerWord));
        sc.close();

        if (playerWord - 1 == computerWord)
            System.out.println("\nThe game was ended in a draw!");
        else if (Math.abs(playerWord - 1 - computerWord) < Math.floor(gameWords.size() / 2)) {
            if (playerWord - 1 > computerWord)
                System.out.println("\nPlayer win!");
            else
                System.out.println("\nComputer win!");
        } else if (Math.abs(playerWord - 1 - computerWord) >= Math.floor(gameWords.size() / 2)) {
            if (playerWord - 1 < computerWord)
                System.out.println("\nPlayer win!");
            else
                System.out.println("\nComputer win!");
        }
        System.out.println("\nHMAC key: " + key);
    }
}