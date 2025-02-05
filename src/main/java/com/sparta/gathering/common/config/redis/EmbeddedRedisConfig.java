//package com.sparta.gathering.common.config.redis;
//
//import com.sparta.gathering.common.exception.BaseException;
//import com.sparta.gathering.common.exception.ExceptionEnum;
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.util.StringUtils;
//import redis.embedded.RedisServer;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.Objects;
//
//@Slf4j
//@Configuration
//public class EmbeddedRedisConfig(RedisProperties redisProperties) {
//
//    @Value("${spring.redis.port}")
//    private int redisPort;
//    @Value("${spring.redis.maxmemory}")
//    private String redisMaxMemory;
//
//    private RedisServer redisServer;
//
//    @PostConstruct
//    public void startRedis() throws IOException {
//        int port = isRedisRunning() ? findAvailablePort() : redisPort;
//        if (isArmArchitecture()) {
//            log.info("ARM Architecture");
//            redisServer = new RedisServer(Objects.requireNonNull(getRedisServerExecutable()), port);
//        } else {
//            redisServer = RedisServer.builder()
//                    .port(port)
//                    .setting("maxmemory " + redisMaxMemory)
//                    .build();
//        }
//        redisServer.start();
//    }
//
//    @PreDestroy
//    public void stopRedis() {
//        redisServer.stop();
//    }
//
//    public int findAvailablePort() throws IOException {
//        for (int port = 10000; port <= 65535; port++) {
//            Process process = executeGrepProcessCommand(port);
//            if (!isRunning(process)) {
//                return port;
//            }
//        }
//
//        throw new BaseException(ExceptionEnum.NOT_FOUND_AVAILABLE_PORT);
//    }
//
//    private boolean isRedisRunning() throws IOException {
//        return isRunning(executeGrepProcessCommand(redisPort));
//    }
//
//    private Process executeGrepProcessCommand(int redisPort) throws IOException {
//        String command = String.format("netstat -nat | grep LISTEN|grep %d", redisPort);
//        String[] shell = {"/bin/sh", "-c", command};
//
//        return Runtime.getRuntime().exec(shell);
//    }
//
//    private boolean isRunning(Process process) {
//        String line;
//        StringBuilder pidInfo = new StringBuilder();
//
//        try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//            while ((line = input.readLine()) != null) {
//                pidInfo.append(line);
//            }
//        } catch (Exception e) {
//            throw new BaseException(ExceptionEnum.ERROR_EXECUTING_EMBEDDED_REDIS);
//        }
//        return StringUtils.hasText(pidInfo.toString());
//    }
//
//    private File getRedisServerExecutable() {
//        try {
//            return new File("src/main/resources/binary/redis/redis-server-6.2.5-mac-arm64");
//        } catch (Exception e) {
//            throw new BaseException(ExceptionEnum.REDIS_SERVER_EXCUTABLE_NOT_FOUND);
//        }
//    }
//
//    private boolean isArmArchitecture() {
//        return System.getProperty("os.arch").contains("aarch64");
//    }
//}
