package com.kinkle.helloquick.udp.protocol;

import com.kinkle.helloquick.udp.util.ByteBufferUtil;
import com.kinkle.helloquick.udp.util.TimestampUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.nio.ByteBuffer;

/**
 * UDP协议包头结构类
 *
 * <p>表示UDP协议的固定长度包头，包含魔数、版本号、协议号、编码格式、加密方式、
 * 包体长度、时间戳和保留字段。CRC32校验值位于包体之后。</p>
 *
 * @author kinkle
 * @version 1.0
 * @since 2025-08-31
 */
@Data
@EqualsAndHashCode
@ToString
public class ProtocolHeader {
    
    /**
     * 魔数
     */
    private int magic;
    
    /**
     * 协议版本号
     */
    private byte version;
    
    /**
     * 协议号
     */
    private short protocolId;
    
    /**
     * 编码格式
     */
    private byte encoding;
    
    /**
     * 加密方式
     */
    private byte encryption;
    
    /**
     * 包体长度
     */
    private int bodyLength;
    

    
    /**
     * 时间戳（毫秒）
     */
    private long timestamp;
    
    /**
     * 保留字段
     */
    private byte[] reserved;
    
    /**
     * 默认构造函数
     */
    public ProtocolHeader() {
        this.magic = ProtocolConstants.MAGIC_NUMBER;
        this.version = ProtocolConstants.PROTOCOL_VERSION;
        this.encoding = ProtocolConstants.Encoding.JSON;
        this.encryption = ProtocolConstants.Encryption.NONE;
        this.bodyLength = 0;
        this.timestamp = TimestampUtil.currentTimestamp();
        this.reserved = new byte[ProtocolConstants.HeaderLengths.RESERVED];
    }
    
    /**
     * 构造函数
     * 
     * @param protocolId 协议号
     * @param bodyLength 包体长度
     */
    public ProtocolHeader(short protocolId, int bodyLength) {
        this();
        this.protocolId = protocolId;
        this.bodyLength = bodyLength;
    }
    
    /**
     * 构造函数
     * 
     * @param protocolId 协议号
     * @param encoding 编码格式
     * @param encryption 加密方式
     * @param bodyLength 包体长度
     */
    public ProtocolHeader(short protocolId, byte encoding, byte encryption, int bodyLength) {
        this();
        this.protocolId = protocolId;
        this.encoding = encoding;
        this.encryption = encryption;
        this.bodyLength = bodyLength;
    }
    

    
    /**
     * 验证包头是否有效
     * 
     * @return 是否有效
     */
    public boolean isValid() {
        return magic == ProtocolConstants.MAGIC_NUMBER
            && version == ProtocolConstants.PROTOCOL_VERSION
            && bodyLength >= ProtocolConstants.MIN_BODY_LENGTH
            && bodyLength <= ProtocolConstants.MAX_BODY_LENGTH
            && reserved != null
            && reserved.length == ProtocolConstants.HeaderLengths.RESERVED;
    }
    
    /**
     * 验证魔数是否正确
     * 
     * @return 魔数是否正确
     */
    public boolean isValidMagic() {
        return magic == ProtocolConstants.MAGIC_NUMBER;
    }
    
    /**
     * 验证版本号是否支持
     * 
     * @return 版本号是否支持
     */
    public boolean isValidVersion() {
        return version == ProtocolConstants.PROTOCOL_VERSION;
    }
    
    /**
     * 验证包体长度是否合理
     * 
     * @return 包体长度是否合理
     */
    public boolean isValidBodyLength() {
        return bodyLength >= ProtocolConstants.MIN_BODY_LENGTH
            && bodyLength <= ProtocolConstants.MAX_BODY_LENGTH;
    }
    

    
    /**
     * 将包头序列化为字节数组
     *
     * @return 字节数组
     */
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBufferUtil.allocate(ProtocolConstants.HEADER_LENGTH);

        // 写入各个字段
        ByteBufferUtil.putInt(buffer, magic);
        ByteBufferUtil.putByte(buffer, version);
        ByteBufferUtil.putShort(buffer, protocolId);
        ByteBufferUtil.putByte(buffer, encoding);
        ByteBufferUtil.putByte(buffer, encryption);
        ByteBufferUtil.putInt(buffer, bodyLength);
        ByteBufferUtil.putLong(buffer, timestamp);
        ByteBufferUtil.putBytes(buffer, reserved);

        return ByteBufferUtil.toBytes(buffer);
    }
    
    /**
     * 从字节数组反序列化包头
     *
     * @param data 字节数组
     * @return 包头对象
     */
    public static ProtocolHeader fromBytes(byte[] data) {
        if (data == null || data.length < ProtocolConstants.HEADER_LENGTH) {
            throw new IllegalArgumentException("Invalid header data length");
        }

        ByteBuffer buffer = ByteBufferUtil.wrap(data);

        ProtocolHeader header = new ProtocolHeader();
        header.magic = ByteBufferUtil.getInt(buffer);
        header.version = ByteBufferUtil.getByte(buffer);
        header.protocolId = ByteBufferUtil.getShort(buffer);
        header.encoding = ByteBufferUtil.getByte(buffer);
        header.encryption = ByteBufferUtil.getByte(buffer);
        header.bodyLength = ByteBufferUtil.getInt(buffer);
        header.timestamp = ByteBufferUtil.getLong(buffer);

        // 读取保留字段
        header.reserved = new byte[ProtocolConstants.HeaderLengths.RESERVED];
        buffer.get(header.reserved);

        return header;
    }
    
    /**
     * 从ByteBuffer反序列化包头
     *
     * @param buffer ByteBuffer
     * @return 包头对象
     */
    public static ProtocolHeader fromByteBuffer(ByteBuffer buffer) {
        if (buffer == null || buffer.remaining() < ProtocolConstants.HEADER_LENGTH) {
            throw new IllegalArgumentException("Invalid buffer or insufficient data");
        }

        ProtocolHeader header = new ProtocolHeader();
        header.magic = ByteBufferUtil.getInt(buffer);
        header.version = ByteBufferUtil.getByte(buffer);
        header.protocolId = ByteBufferUtil.getShort(buffer);
        header.encoding = ByteBufferUtil.getByte(buffer);
        header.encryption = ByteBufferUtil.getByte(buffer);
        header.bodyLength = ByteBufferUtil.getInt(buffer);
        header.timestamp = ByteBufferUtil.getLong(buffer);

        // 读取保留字段
        header.reserved = new byte[ProtocolConstants.HeaderLengths.RESERVED];
        buffer.get(header.reserved);

        return header;
    }

    /**
     * 将包头序列化为ByteBuffer
     *
     * @return 包含包头数据的ByteBuffer
     */
    public ByteBuffer toByteBuffer() {
        ByteBuffer buffer = ByteBufferUtil.allocate(ProtocolConstants.HEADER_LENGTH);

        // 写入各个字段
        ByteBufferUtil.putInt(buffer, magic);
        ByteBufferUtil.putByte(buffer, version);
        ByteBufferUtil.putShort(buffer, protocolId);
        ByteBufferUtil.putByte(buffer, encoding);
        ByteBufferUtil.putByte(buffer, encryption);
        ByteBufferUtil.putInt(buffer, bodyLength);
        ByteBufferUtil.putLong(buffer, timestamp);
        ByteBufferUtil.putBytes(buffer, reserved);

        buffer.flip(); // 切换到读模式
        return buffer;
    }

}
