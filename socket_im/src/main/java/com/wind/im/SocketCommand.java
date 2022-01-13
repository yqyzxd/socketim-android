package com.wind.im;

import com.wind.im.util.ByteBufferUtil;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据包封装类
 */
public class SocketCommand {
    private List<Byte> commandList = new ArrayList<>();
    private byte[] commandBytes;
    public static SocketCommand ofCmd(int op,byte[] dataBytes) {

       // byte[] dataBytes=data.getBytes(Charset.defaultCharset());
        int dataLen=dataBytes.length;
        List<Byte> bytes=new ArrayList<>();
        byte[] dataLenBytes= ByteBufferUtil.getBytes(dataLen, ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < dataLenBytes.length; i++) {
            bytes.add(dataLenBytes[i]);
        }
        byte[] opBytes=ByteBufferUtil.getBytes(op, ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < opBytes.length; i++) {
            bytes.add(opBytes[i]);
        }

        for (int i = 0; i < dataBytes.length; i++) {
            bytes.add(dataBytes[i]);
        }


        SocketCommand cmd=new SocketCommand();
        cmd.addCommand(listToByteArray(bytes));

        return cmd;
    }


    private static byte[] listToByteArray(List<Byte> bytes){
        byte[] cmd = new byte[bytes.size()];
        for (int i=0;i<bytes.size();i++) {
            cmd[i]=bytes.get(i);
        }
        return cmd;
    }
    private void addCommand(byte[] command) {
        for (int i = 0; i < command.length; i++) {
            commandList.add(Byte.valueOf(command[i]));
        }
        commandBytes= command;

    }

    public byte[] getCommandBytes() {
        return commandBytes;
    }

    public List<Byte> getCommandList() {
        return commandList;
    }
}
