package com.helium.nettymq.broker.utils;

import com.alibaba.fastjson.JSON;
import com.helium.nettymq.broker.model.MqTopicModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

public class FileContentReaderUtils {

    public static String readFromFile(String path) {
        try (BufferedReader in = new BufferedReader(new FileReader(path))) {
            StringBuffer stb = new StringBuffer();
            while (in.ready()) {
                stb.append(in.readLine());
            }
            return stb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String content = FileContentReaderUtils.readFromFile("C:\\Programming\\programming-works\\github-projects\\Java\\eaglemq\\nettymq\\broker\\config\\nettymq-topic.json");
        System.out.println(content);

        List<MqTopicModel> mqTopicModelList = JSON.parseArray(content, MqTopicModel.class);
        System.out.println(mqTopicModelList);
    }
}
