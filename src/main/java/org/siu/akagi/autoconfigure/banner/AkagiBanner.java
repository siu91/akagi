package org.siu.akagi.autoconfigure.banner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;


/**
 * 启动打印banner
 *
 * @Author Siu
 * @Date 2020/3/28 20:53
 * @Version 0.0.1
 */
@Slf4j
public class AkagiBanner {

    private static final String[] BANNER = {
            "",
            "  ___    _                      ",
            " / _ \\  | |                    (_)",
            "/ /_\\ \\ | | __   __ _    __ _   _ ",
            "|  _  | | |/ /  / _  |  / _  | | |",
            "| | | | |   <  | (_| | | (_| | | |",
            "\\_| |_/ |_|\\_\\  \\__,_|  \\__, | |_|",
            "                         __/ |    ",
            "                        |___/     ",
            ""};

    private static final String APP = " :: Akagi :: ";

    private static final int STRAP_LINE_SIZE = 42;

    public static void printBanner() {
        for (String line : BANNER) {
           log.info(line);
        }
        String version = Version.getVersion();
        version = (version != null) ? " (v" + version + ")" : "";
        StringBuilder padding = new StringBuilder();
        while (padding.length() < STRAP_LINE_SIZE - (version.length() + APP.length())) {
            padding.append(" ");
        }

       log.info(AnsiOutput.toString(AnsiColor.GREEN, APP, AnsiColor.DEFAULT, padding.toString(),
                AnsiStyle.FAINT, version));
       log.info("Akagi let your springboot application quickly integrate authentication and authorization");
       log.info("https://github.com/siu91/akagi");
    }
}
