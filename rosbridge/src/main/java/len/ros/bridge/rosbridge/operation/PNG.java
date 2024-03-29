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
package len.ros.bridge.rosbridge.operation;

import len.ros.bridge.message.MessageType;

@MessageType(string = "png")
public class PNG extends Operation {
    public String data;
    public Integer num;     // use Integer for optional items
    public Integer total;   // use Integer for optional items

    public PNG() {
    }

    public PNG(String data) {
        this.data = data;
    }

    public PNG(String data, int num, int total) {
        this.data = data;
        this.num = num;
        this.total = total;
    }
}
