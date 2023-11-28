package lab.fcpsr.suprime.utils;

import java.util.Optional;

public class CustomFileUtil {
    private static final float ONE_MB = 1048576.0f;
    public static Optional<String> getExtension(String filename){
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public static float getMegaBytes(int bitsAmount){
        return bitsAmount / ONE_MB;
    }
}
