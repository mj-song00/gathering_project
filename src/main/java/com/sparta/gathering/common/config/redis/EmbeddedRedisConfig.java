//package com.sparta.gathering.common.config.redis;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import redis.embedded.RedisServer;
//
//import java.io.IOException;
//import java.net.ServerSocket;
//
//@Slf4j
//@Configuration
//public class EmbeddedRedisConfig {
//
//    @Value("${spring.data.redis.port}")
//    private int port;
//
//    @Value("${spring.data.redis.maxmemory}")
//    private int maxmemorySize;
//
//    private RedisServer redisServer;
//
//
//
//    @PostConstruct
//    public void startRedis() throws IOException {
//        int port = isRedisRunning() ? findAvailablePort() : port;
//        redisServer = new RedisServer(port);
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
//        throw new BusinessLogicException(ExceptionCode.NOT_FOUND_AVAILABLE_PORT);
//    }
//
//    /**
//     * Embedded Redis가 현재 실행중인지 확인
//     */
//    private boolean isRedisRunning() throws IOException {
//        return isRunning(executeGrepProcessCommand(redisPort));
//    }
//
//    /**
//     * 해당 port를 사용중인 프로세스를 확인하는 sh 실행
//     */
//    private Process executeGrepProcessCommand(int redisPort) throws IOException {
//        String command = String.format("netstat -nat | grep LISTEN|grep %d", redisPort);
//        String[] shell = {"/bin/sh", "-c", command};
//
//        return Runtime.getRuntime().exec(shell);
//    }
//
//    /**
//     * 해당 Process가 현재 실행중인지 확인
//     */
//    private boolean isRunning(Process process) {
//        String line;
//        StringBuilder pidInfo = new StringBuilder();
//
//        try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//            while ((line = input.readLine()) != null) {
//                pidInfo.append(line);
//            }
//        } catch (Exception e) {
//            throw new BusinessLogicException(ExceptionCode.ERROR_EXECUTING_EMBEDDED_REDIS);
//        }
//        return StringUtils.hasText(pidInfo.toString());
//    }
//}