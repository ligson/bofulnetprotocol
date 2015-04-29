package com.boful.net.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class CommandLineUtils {

    private static Logger logger = Logger.getLogger(CommandLineUtils.class);
    private static Options options = null;

    /**
     * 命令行解析
     * 
     * @param cmd
     * @return
     */
    public static Map<String, String> parse(String cmd) {
        logger.debug("开始解析命令行");
        // 将命令行按空格分割
        String[] cmdArgs = StringUtils.split(cmd, " ");
        CommandLineParser parser = new BasicParser();
        if (options == null) {
            initOptions();
        }
        Map<String, String> commandMap = null;
        try {
            CommandLine commandLine = parser.parse(options, cmdArgs);

            commandMap = new HashMap<String, String>();
            if (commandLine.hasOption("operation")) {
                commandMap.put("operation", commandLine.getOptionValue("operation"));
            }
            if (commandLine.hasOption("i")) {
                commandMap.put("diskFile", commandLine.getOptionValue("i"));
            }
            if (commandLine.hasOption("o")) {
                commandMap.put("destFile", commandLine.getOptionValue("destFile"));
            }
            if (commandLine.hasOption("id")) {
                commandMap.put("jobid", commandLine.getOptionValue("id"));
            }
            if (commandLine.hasOption("vb")) {
                commandMap.put("videoBitrate", commandLine.getOptionValue("vb"));
            }
            if (commandLine.hasOption("ab")) {
                commandMap.put("audioBitrate", commandLine.getOptionValue("ab"));
            }
            if (commandLine.hasOption("size")) {
                commandMap.put("size", commandLine.getOptionValue("size"));
            }

            logger.debug("命令行解析成功！");
        } catch (Exception e) {
            logger.debug("命令行解析失败！");
            logger.debug("命令行解析错误信息：" + e.getMessage());
        }
        return commandMap;
    }

    /**
     * 初始化命令行参数
     */
    private static void initOptions() {
        options = new Options();
        // 操作
        options.addOption("operation", "operation", true, "");
        // 输入文件
        options.addOption("i", "diskFile", true, "");
        // 输出文件
        options.addOption("o", "destFile", true, "");
        // jobId
        options.addOption("id", "jobId", true, "");
        // 视频转码率
        options.addOption("vb", "videoBitrate", true, "");
        // 音频转码率
        options.addOption("ab", "audioBitrate", true, "");
        // 视频size
        options.addOption("size", "size", true, "");
    }

    public static String checkCmdOnlyFile(Map<String, String> commandMap) {
        if (commandMap.get("diskFile") == null) {
            return "没有设置diskFile！";
        }
        if (commandMap.get("destFile") == null) {
            return "没有设置destFile！";
        }

        File diskFile = new File(commandMap.get("diskFile"));
        if (!diskFile.exists()) {
            return "文件" + diskFile.getAbsolutePath() + "不存在！";
        }

        return null;
    }
}
