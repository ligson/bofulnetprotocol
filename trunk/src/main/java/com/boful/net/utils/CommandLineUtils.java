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

import com.boful.common.file.utils.FileType;
import com.boful.common.file.utils.FileUtils;

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
            } else {
                commandMap.put("videoBitrate", "0");
            }
            if (commandLine.hasOption("ab")) {
                commandMap.put("audioBitrate", commandLine.getOptionValue("ab"));
            } else {
                commandMap.put("audioBitrate", "0");
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

    public static String checkCmd(Map<String, String> commandMap) {

        // 必须项目验证 START
        if (commandMap.get("operation") == null) {
            return "没有设置operation！";
        }

        if (commandMap.get("jobid") == null) {
            return "没有设置jobId！";
        }

        if (commandMap.get("diskFile") == null) {
            return "没有设置diskFile！";
        }

        File diskFile = new File(commandMap.get("diskFile"));
        if (!diskFile.exists()) {
            return "文件" + diskFile.getAbsolutePath() + "不存在！";
        }

        if (commandMap.get("destFile") == null) {
            return "没有设置destFile！";
        }
        File destFile = new File(commandMap.get("destFile"));

        int videoBitrate = 0;
        try {
            videoBitrate = Integer.parseInt(commandMap.get("videoBitrate"));
            if (videoBitrate < 0) {
                return "参数videoBitrate的值必须是正数！";
            }
        } catch (Exception e) {
            return "参数videoBitrate的值必须是数值！";
        }

        int audioBitrate = 0;
        try {
            audioBitrate = Integer.parseInt(commandMap.get("audioBitrate"));
            if (audioBitrate < 0) {
                return "参数audioBitrate的值必须是正数！";
            }
        } catch (Exception e) {
            return "参数audioBitrate的值必须是数值！";
        }

        int width = 0;
        int height = 0;
        String size = commandMap.get("size");
        if (size != null) {
            String[] array = size.split("x");
            if (array.length != 2) {
                return "参数size的值错误！";
            }

            try {
                width = Integer.parseInt(array[0]);
                height = Integer.parseInt(array[1]);
                if (width < 0 || height < 0) {
                    return "参数size的值错误！";
                }
            } catch (NumberFormatException e) {
                return "参数size的值错误！";
            }
        }
        // 必须项目验证 END

        // 关联项目验证 START

        String diskSufix = FileUtils.getFileSufix(diskFile.getName());
        diskSufix = diskSufix.toUpperCase();
        String destSufix = FileUtils.getFileSufix(destFile.getName());
        destSufix = destSufix.toUpperCase();

        // 元文件为视频
        if (FileType.isVideo(diskFile.getName())) {
            if (videoBitrate == 0 || audioBitrate == 0 || width == 0 || height == 0) {
                return "视频文件转码必须设置参数:videoBitrate、audioBitrate和size！";
            }
            if (!FileType.isVideo(destFile.getName())) {
                return "参数destFile不是视频文件！";
            }
        }

        // 元文件为音频
        else if (FileType.isAudio(diskFile.getName())) {
            if (audioBitrate == 0) {
                return "音频文件转码必须设置参数:audioBitrate！";
            }
            if (!FileType.isAudio(destFile.getName())) {
                return "参数destFile不是音频文件！";
            }
        }

        // 文档转码
        else if (FileType.isDocument(diskFile.getName()) || diskSufix.equals("PDF") || diskSufix.equals("SWF")) {
            // PDF文件只能转码为SWF文件
            if (diskSufix.equals("PDF") && !destSufix.equals("SWF")) {
                return "只有PDF文件能够转码为SWF文件！";
            }

            // SWF文件不能转码为PDF文件
            if (diskSufix.equals("SWF") && destSufix.equals("PDF")) {
                return "SWF文件不能转码为PDF文件！";
            }
        }

        // 图片转码
        else if (FileType.isImage(diskFile.getName())) {
            if (!FileType.isImage(destFile.getName())) {
                return "参数destFile不是图片文件！";
            }
        }

        // 其他类型
        else {
            return "转码的文件类型错误，只能是视频、音频、文档和图片！";
        }

        // 关联项目验证 END

        return null;
    }
}
