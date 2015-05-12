package com.boful.net.cbalance.protocol;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.mina.core.buffer.IoBuffer;

import com.boful.net.cnode.protocol.Operation;

public class DistributeServerProtocol {
    public static int OPERATION = Operation.DISTRIBUTE_CONVERT_SEVER;

    private String serverIp;
    private int fServerPort;
    private int cNodePort;

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public int getfServerPort() {
        return fServerPort;
    }

    public void setfServerPort(int fServerPort) {
        this.fServerPort = fServerPort;
    }

    public int getcNodePort() {
        return cNodePort;
    }

    public void setcNodePort(int cNodePort) {
        this.cNodePort = cNodePort;
    }

    public int countLength() {
        // TAG+IPBUFFERLEN+IPBUFFER+FSERVERPORT+CNODEPORT
        try {
            byte[] ipBuffer = serverIp.getBytes("UTF-8");
            return 4 + 4 + ipBuffer.length + 4 + 4;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // 编码
    public IoBuffer toByteArray() throws IOException {
        IoBuffer ioBuffer = IoBuffer.allocate(countLength());
        ioBuffer.putInt(OPERATION);
        ioBuffer.putInt(serverIp.getBytes("UTF-8").length);
        ioBuffer.put(serverIp.getBytes("UTF-8"));
        ioBuffer.putInt(fServerPort);
        ioBuffer.putInt(cNodePort);
        return ioBuffer;
    }

    // 解码
    public static DistributeServerProtocol parse(IoBuffer ioBuffer) throws IOException {
        if (ioBuffer.remaining() < 4) {
            return null;
        }

        DistributeServerProtocol distributeServerProtocol = new DistributeServerProtocol();
        int ipLen = ioBuffer.getInt();
        byte[] ipBuffer = new byte[ipLen];
        ioBuffer.get(ipBuffer);
        distributeServerProtocol.setServerIp(new String(ipBuffer, "UTF-8"));
        distributeServerProtocol.setfServerPort(ioBuffer.getInt());
        distributeServerProtocol.setcNodePort(ioBuffer.getInt());
        return distributeServerProtocol;
    }
}
