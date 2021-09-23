package com.example.create_db_file.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Slf4j
@Component
public class CleanTemporalFile {

    private final ResourceLoader resourceLoader;

    //    @Scheduled(fixedDelay = 1000)

    /**
     * 10分起きに一時ファイルディレクトリに格納されているファイルを精査し
     * 保存してから10分以上経過しているファイルを削除するメソッド
     */
    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void sampleTask(){
        log.info("hello scheduled task!!");
        Resource resource = resourceLoader.getResource("classpath:temporal");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String formatNowDate = LocalDateTime.now().format(formatter);
        try {
            Files.walk(resource.getFile().toPath()).filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        log.info(path.toString());
                        log.info(path.getFileName().toString());
                        log.info(StringUtils.substring(path.getFileName().toString(),0, 12));
                        String temporalFileInfo = StringUtils.substring(path.getFileName().toString(),0, 12);
                        long timeLag = Long.valueOf(formatNowDate) - Long.valueOf(temporalFileInfo);
                        if(timeLag >= 10){
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
