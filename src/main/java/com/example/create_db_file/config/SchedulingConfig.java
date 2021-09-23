package com.example.create_db_file.config;

import com.example.create_db_file.schedule.CleanTemporalFile;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // スケジュールを有効化
@Configuration
@ComponentScan({"com.example.create_db_file.schedule"})
public class SchedulingConfig {
}
