/**
 * Copyright (c) 2014 Jilk Systems, Inc.
 * <p>
 * This file is part of the Java ROSBridge Client.
 * <p>
 * The Java ROSBridge Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * The Java ROSBridge Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with the Java ROSBridge Client.  If not, see http://www.gnu.org/licenses/.
 */
package len.ros.bridge;

import len.ros.bridge.message.Clock;
import len.ros.bridge.message.Log;
import len.ros.bridge.rosapi.message.Empty;
import len.ros.bridge.rosapi.message.GetTime;
import len.ros.bridge.rosapi.message.MessageDetails;
import len.ros.bridge.rosapi.message.Type;
import len.ros.bridge.rosbridge.ROSBridgeClient;

public class Example {

    public Example() {
    }

    public static void main(String[] args) {
        ROSBridgeClient client = new ROSBridgeClient("ws://162.243.238.80:9090");
        client.connect();
        try {
            testTopic(client);
//            testService(client);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        } finally {
            client.disconnect();
        }
    }

    public static void testService(ROSBridgeClient client) {
        try {
            Service<Empty, GetTime> timeService =
                    new Service<Empty, GetTime>("/rosapi/get_time", Empty.class, GetTime.class, client);
            timeService.verify();
            //System.out.println("Time (secs): " + timeService.callBlocking(new Empty()).time.sec);

            Service<len.ros.bridge.rosapi.message.Service, Type> serviceTypeService =
                    new Service<len.ros.bridge.rosapi.message.Service, Type>("/rosapi/service_type",
                            len.ros.bridge.rosapi.message.Service.class, Type.class, client);
            serviceTypeService.verify();
            String type = serviceTypeService.callBlocking(new len.ros.bridge.rosapi.message.Service("/rosapi/service_response_details")).type;

            Service<Type, MessageDetails> serviceDetails =
                    new Service<Type, MessageDetails>("/rosapi/service_response_details",
                            Type.class, MessageDetails.class, client);
            serviceDetails.verify();
            serviceDetails.callBlocking(new Type(type)).print();

            len.ros.bridge.Topic<Log> logTopic =
                    new len.ros.bridge.Topic<Log>("/rosout", Log.class, client);
            logTopic.verify();
            
            /*
            System.out.println("Nodes");
            for (String s : client.getNodes())
                System.out.println("    " + s);
            System.out.println("Topics");
            for (String s : client.getTopics()) {
                System.out.println(s + ":");
                client.getTopicMessageDetails(s).print();
            }
            System.out.println("Services");
            for (String s : client.getServices()) {
                System.out.println(s + ":");
                client.getServiceRequestDetails(s).print();
                System.out.println("-----------------");
                client.getServiceResponseDetails(s).print();
            }
            */
        } catch (InterruptedException ex) {
            System.out.println("Process was interrupted.");
        }
        /*
        Service<Empty, Topics> topicService =
                new Service<Empty, Topics>("/rosapi/topics", Empty.class, Topics.class, client);
        Service<Topic, Type> typeService =
                new Service<Topic, Type>("/rosapi/topic_type", Topic.class, Type.class, client);
        Service<Type, MessageDetails> messageService =
                new Service<Type, MessageDetails>("/rosapi/message_details", Type.class, MessageDetails.class, client);
        try {
            Topics topics = topicService.callBlocking(new Empty());
            for (String topicString : topics.topics) {
                Topic topic = new Topic();
                topic.topic = topicString;
                Type type = typeService.callBlocking(topic);
                MessageDetails details = messageService.callBlocking(type);
                System.out.println("Topic: " + topic.topic + " Type: " + type.type);
                details.print();
                System.out.println();
            }
            Type type = new Type();
            type.type = "time";
            System.out.print("Single type check on \'time\': ");
            messageService.callBlocking(type).print();
        }
        catch (InterruptedException ex) {
            System.out.println("testService: process was interrupted.");
        }
        */
    }

    public static void testTopic(ROSBridgeClient client) {
        len.ros.bridge.Topic<Clock> clockTopic = new len.ros.bridge.Topic<Clock>("/clock", Clock.class, client);
        clockTopic.subscribe();
        try {
            Thread.sleep(20000);
        } catch (InterruptedException ex) {
        }
        Clock cl = null;
        try {
            cl = clockTopic.take(); // just gets one
        } catch (InterruptedException ex) {
        }
        cl.print();
        cl.clock.nsecs++;
        clockTopic.unsubscribe();
        clockTopic.advertise();
        clockTopic.publish(cl);
        clockTopic.unadvertise();
    }
}
