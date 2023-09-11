package com.hekai.webSocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class WebSocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebSocketApplication.class, args);
        WebSocket webSocket = new WebSocket();
        Scanner sc = new Scanner(System.in);
        while (true){
            //操作
            String op = sc.nextLine();
            if (op.equals("单发")){
                System.out.println("请输入用户ID");
                String id = sc.nextLine();
                System.out.println("请输入消息");
                String message = sc.nextLine();
                webSocket.sendOneMessage(id, message);
            } else if (op.equals("群发")) {
                System.out.println("请输入消息");
                String message = sc.nextLine();
                webSocket.sendAllMessage(message);
            } else if (op.equals("多发")) {
                String[] userIds = new String[6];
                System.out.println("请输入用户ID, 输入quit则退出输入, 最多输入五个");
                int index = 0;
                String id = sc.nextLine();
                while (! id.equals("quit") && index != 5) {
                    userIds[index++] = id;
                    System.out.println("请继续输入用户ID, 输入quit则退出输入");
                    id = sc.nextLine();
                    index++;
                }

                System.out.println("请输入消息内容");
                String message = sc.nextLine();
                System.out.println("多发用户为: " + userIds + "\n消息为: " + message);
                webSocket.sendMoreMessage(userIds, message);
            }
        }
    }

}
