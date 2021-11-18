package com.example.create_db_file.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Slf4j
@Component
public class CleanTemporalFile {

    private final ResourceLoader resourceLoader;

    /**
     * 10分起きに一時ファイルディレクトリに格納されているファイルを精査し
     * 保存してから10分以上経過しているファイルを削除するメソッド
     */
    @Scheduled(fixedDelay = 1000 * 60 * 10)
//    @Scheduled(fixedDelay = 1000 * 60)
    public void sampleTask(){
        log.info("hello scheduled task!!");
        Resource resource = resourceLoader.getResource("classpath:temporal");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String formatNowDate = LocalDateTime.now().format(formatter);

        File file = new File("/tmp");

        try {

//            long fileCount = Files.walk(resource.getFile().toPath()).count();
            long fileCount = Files.walk(file.toPath()).count();
            log.info("現在の格納ファイル数: {}", fileCount);
            Files.walk(file.toPath()).filter(path -> !Files.isDirectory(path))
//            Files.walk(resource.getFile().toPath()).filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {

                        if(path.getFileName().toString().length() > 12 && path.getFileName().toString().substring(0, 12).matches("[0-9]*")){
                            String temporalFileInfo = StringUtils.substring(path.getFileName().toString(),0, 12);
                            long timeLag = Long.parseLong(formatNowDate) - Long.parseLong(temporalFileInfo);
                            // if(timeLag >= 1)
                            if(timeLag >= 10){
                                try {
                                    Files.delete(path);
                                    log.info("ファイル名: {} を削除しました", path.getFileName());
                                } catch (IOException e) {
                                    log.info("ファイル削除処理中にエラー発生",e);
                                }
                            }

                        } else {
                            log.info("削除対象ファイルでは無いため無視されました: 無視されたファイル -> {}", path.getFileName());
                        }
                    });
        } catch (IOException e) {
            log.info("ファイル削除処理中にエラー発生",e);
        }
    }
}
