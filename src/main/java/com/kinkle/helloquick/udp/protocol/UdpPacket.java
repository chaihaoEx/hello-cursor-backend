package com.kinkle.helloquick.udp.protocol;

import com.kinkle.helloquick.udp.util.ByteBufferUtil;
import com.kinkle.helloquick.udp.util.Crc32Util;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.nio.ByteBuffer;

/**
 * UDP协议完整数据包类
 *
 * <p>表示完整的UDP协议数据包，包含包头、包体和CRC32校验值。
 * 包结构：包头(25字节) + 包体(N字节) + CRC32(4字节)</p>
 *
 * @author kinkle
 * @version 1.0
 * @since 2025-08-31
 */
@Data
@EqualsAndHashCode
@ToString
public class UdpPacket {

    /**
     * 协议包头
     */
    private ProtocolHeader header;

    /**
     * 包体数据
     */
    private byte[] body;

    /**
     * CRC32校验值
     */
    private int crc32;

    /**
     * 默认构造函数
     */
    public UdpPacket() {
        this.header = new ProtocolHeader();
        this.body = new byte[0];
        this.crc32 = 0;
    }

    /**
     * 构造函数
     *
     * @param header 协议包头
     * @param body 包体数据
     */
    public UdpPacket(ProtocolHeader header, byte[] body) {
        if (header == null) {
            throw new IllegalArgumentException("Header cannot be null");
        }
        if (body == null) {
            throw new IllegalArgumentException("Body cannot be null");
        }

        this.header = header;
        this.body = body;
        this.crc32 = 0;

        // 设置包头中的包体长度
        if (this.header.getBodyLength() == 0) {
            this.header.setBodyLength(this.body.length);
        }
    }

    /**
     * 构造函数
     *
     * @param protocolId 协议号
     * @param body 包体数据
     */
    public UdpPacket(short protocolId, byte[] body) {
        this(new ProtocolHeader(protocolId, body != null ? body.length : 0), body != null ? body : new byte[0]);
    }

    /**
     * 计算并设置CRC32值
     */
    public void calculateAndSetCrc32() {
        if (header == null) {
            throw new IllegalStateException("Header cannot be null");
        }

        try {
            byte[] headerData = header.toBytes();
            long crc32Value = Crc32Util.calculate(headerData, body);
            this.crc32 = (int) crc32Value;
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate CRC32", e);
        }
    }

    /**
     * 验证CRC32值
     *
     * @return CRC32值是否正确
     */
    public boolean verifyCrc32() {
        if (header == null) {
            return false;
        }

        try {
            byte[] headerData = header.toBytes();
            long expectedCrc32 = Crc32Util.calculate(headerData, body);
            return this.crc32 == (int) expectedCrc32;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证包的完整性
     *
     * @return 包是否完整有效
     */
    public boolean isValid() {
        if (header == null || body == null) {
            return false;
        }

        // 验证包头
        if (!header.isValid()) {
            return false;
        }

        // 验证包体长度
        if (body.length != header.getBodyLength()) {
            return false;
        }

        // 验证CRC32
        return verifyCrc32();
    }

    /**
     * 将完整UDP包序列化为字节数组
     *
     * @return 字节数组
     */
    public byte[] toBytes() {
        if (header == null) {
            throw new IllegalStateException("Header cannot be null");
        }
        if (body == null) {
            throw new IllegalStateException("Body cannot be null");
        }

        // 计算CRC32
        calculateAndSetCrc32();

        int totalLength = ProtocolConstants.HEADER_LENGTH + body.length + ProtocolConstants.HeaderLengths.CRC32;
        ByteBuffer buffer = ByteBufferUtil.allocate(totalLength);

        // 写入包头
        byte[] headerBytes = header.toBytes();
        ByteBufferUtil.putBytes(buffer, headerBytes);

        // 写入包体
        ByteBufferUtil.putBytes(buffer, body);

        // 写入CRC32
        ByteBufferUtil.putInt(buffer, crc32);

        return ByteBufferUtil.toBytes(buffer);
    }

    /**
     * 从字节数组反序列化UDP包
     *
     * @param data 字节数组
     * @return UDP包对象
     */
    public static UdpPacket fromBytes(byte[] data) {
        if (data == null || data.length < ProtocolConstants.HEADER_LENGTH + ProtocolConstants.HeaderLengths.CRC32) {
            throw new IllegalArgumentException("Invalid packet data length");
        }

        ByteBuffer buffer = ByteBufferUtil.wrap(data);

        // 读取包头
        byte[] headerData = new byte[ProtocolConstants.HEADER_LENGTH];
        buffer.get(headerData);
        ProtocolHeader header = ProtocolHeader.fromBytes(headerData);

        // 验证包体长度
        int expectedBodyLength = header.getBodyLength();
        if (expectedBodyLength < 0 ||
            ProtocolConstants.HEADER_LENGTH + expectedBodyLength + ProtocolConstants.HeaderLengths.CRC32 > data.length) {
            throw new IllegalArgumentException("Invalid body length in header");
        }

        // 读取包体
        byte[] body = new byte[expectedBodyLength];
        if (expectedBodyLength > 0) {
            buffer.get(body);
        }

        // 读取CRC32
        int crc32 = ByteBufferUtil.getInt(buffer);

        UdpPacket packet = new UdpPacket(header, body);
        packet.setCrc32(crc32);

        return packet;
    }

    /**
     * 从ByteBuffer反序列化UDP包
     *
     * @param buffer ByteBuffer
     * @return UDP包对象
     */
    public static UdpPacket fromByteBuffer(ByteBuffer buffer) {
        if (buffer == null || buffer.remaining() < ProtocolConstants.HEADER_LENGTH + ProtocolConstants.HeaderLengths.CRC32) {
            throw new IllegalArgumentException("Invalid buffer or insufficient data");
        }

        // 读取包头
        byte[] headerData = new byte[ProtocolConstants.HEADER_LENGTH];
        buffer.get(headerData);
        ProtocolHeader header = ProtocolHeader.fromBytes(headerData);

        // 验证包体长度
        int expectedBodyLength = header.getBodyLength();
        if (expectedBodyLength < 0 ||
            buffer.remaining() < expectedBodyLength + ProtocolConstants.HeaderLengths.CRC32) {
            throw new IllegalArgumentException("Invalid body length in header");
        }

        // 读取包体
        byte[] body = new byte[expectedBodyLength];
        if (expectedBodyLength > 0) {
            buffer.get(body);
        }

        // 读取CRC32
        int crc32 = ByteBufferUtil.getInt(buffer);

        UdpPacket packet = new UdpPacket(header, body);
        packet.setCrc32(crc32);

        return packet;
    }

    /**
     * 获取完整UDP包的长度
     *
     * @return 包长度
     */
    public int getTotalLength() {
        if (header == null || body == null) {
            return ProtocolConstants.HEADER_LENGTH + ProtocolConstants.HeaderLengths.CRC32;
        }
        return ProtocolConstants.HEADER_LENGTH + body.length + ProtocolConstants.HeaderLengths.CRC32;
    }
}
