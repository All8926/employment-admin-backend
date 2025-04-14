package com.app.project;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class ProjectSuccess implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        StringBuilder commandLog = new StringBuilder();
        commandLog.append("                 鸡你太美\n" +
                "               鸡你实在太美\n" +
                "                鸡你是太美\n" +
                "                 鸡你太美\n" +
                "              实在是太美鸡你\n" +
                "         鸡你 实在是太美鸡你 美\n" +
                "       鸡你  实在是太美鸡美  太美\n" +
                "      鸡你  实在是太美鸡美     太美\n" +
                "    鸡你    实在是太美鸡美      太美\n" +
                "   鸡你    鸡你实在是美太美    美蓝球球\n" +
                "鸡 鸡     鸡你实在是太美      篮球篮球球\n" +
                " 鸡      鸡你太美裆鸡太啊       蓝篮球\n" +
                "         鸡你太美裆裆鸡美\n" +
                "          鸡你美裆  裆鸡美\n" +
                "           鸡太美    鸡太美\n" +
                "            鸡美      鸡美\n" +
                "            鸡美       鸡美\n" +
                "             鸡美       鸡美\n" +
                "             鸡太       鸡太\n" +
                "           金 猴       金猴\n" +
                "           皮 鞋       皮鞋金猴\n" +
                "            金光       金光 大道\n" +
                "           作者wx:     YL586246       \n" +
                "      坤坤保佑       永不宕机     永无BUG");
        System.out.println(commandLog.toString());
    }
}
