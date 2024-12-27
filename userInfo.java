import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public class userInfo {
    String fileName = "userInfo.txt";
    File file = new File(fileName);
    public userInfo() {
    }

    public boolean isExist() {
        return file.exists();
    }

    public void createFile(String name) {
        try {
            file.createNewFile();
            try (FileWriter writer = new FileWriter(fileName, false)) {
                String content = name + "\n" + randomID();
                writer.write(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
        }
    }

    public void changeName(String name) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(fileName));
            lines.set(0, name); 
            Files.write(Paths.get(fileName), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getInfo() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(fileName));
            return new String[] {lines.get(0), lines.get(1)};
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Hàm tạo ID ngẫu nhiên
    public String randomID() {
        Random rand = new Random();
        String characters = "0123456789";
        int length = 10;
        StringBuilder randomString = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            if (i == 2 || i == 6) {
                randomString.append(" ");
                continue;
            }
            int index = rand.nextInt(characters.length());
            randomString.append(characters.charAt(index));
        }

        return randomString.toString();
    }    
}
