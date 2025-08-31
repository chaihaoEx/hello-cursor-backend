package com.kinkle.helloquick.udp.protocol;

import com.kinkle.helloquick.udp.util.ByteBufferUtil;
import com.kinkle.helloquick.udp.util.TimestampUtil;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 协议包头测试类
 * 
 * @author kinkle
 * @version 1.0
 * @since 2025-08-31
 */
class ProtocolHeaderTest {
    
    @Test
    void testDefaultConstructor() {
        ProtocolHeader header = new ProtocolHeader();
        
        assertEquals(ProtocolConstants.MAGIC_NUMBER, header.getMagic());
        assertEquals(ProtocolConstants.PROTOCOL_VERSION, header.getVersion());
        assertEquals(ProtocolConstants.Encoding.JSON, header.getEncoding());
        assertEquals(ProtocolConstants.Encryption.NONE, header.getEncryption());
        assertEquals(0, header.getBodyLength());
        assertTrue(header.getTimestamp() > 0);
        assertNotNull(header.getReserved());
        assertEquals(ProtocolConstants.HeaderLengths.RESERVED, header.getReserved().length);
    }
    
    @Test
    void testConstructorWithProtocolIdAndBodyLength() {
        short protocolId = ProtocolConstants.SystemProtocols.HEARTBEAT;
        int bodyLength = 100;
        
        ProtocolHeader header = new ProtocolHeader(protocolId, bodyLength);
        
        assertEquals(protocolId, header.getProtocolId());
        assertEquals(bodyLength, header.getBodyLength());
        assertTrue(header.isValid());
    }
    
    @Test
    void testConstructorWithAllParameters() {
        short protocolId = ProtocolConstants.BusinessProtocols.USER_LOGIN;
        byte encoding = ProtocolConstants.Encoding.JSON;
        byte encryption = ProtocolConstants.Encryption.AES;
        int bodyLength = 200;
        
        ProtocolHeader header = new ProtocolHeader(protocolId, encoding, encryption, bodyLength);
        
        assertEquals(protocolId, header.getProtocolId());
        assertEquals(encoding, header.getEncoding());
        assertEquals(encryption, header.getEncryption());
        assertEquals(bodyLength, header.getBodyLength());
        assertTrue(header.isValid());
    }
    
    @Test
    void testValidation() {
        ProtocolHeader header = new ProtocolHeader();

        // 默认情况下应该是有效的
        assertTrue(header.isValid());
        assertTrue(header.isValidMagic());
        assertTrue(header.isValidVersion());
        assertTrue(header.isValidBodyLength());

        // 测试无效的魔数
        header.setMagic(0x12345679);
        assertFalse(header.isValidMagic());
        assertFalse(header.isValid());

        // 恢复魔数
        header.setMagic(ProtocolConstants.MAGIC_NUMBER);

        // 测试无效的版本号
        header.setVersion((byte) 0x02);
        assertFalse(header.isValidVersion());
        assertFalse(header.isValid());

        // 恢复版本号
        header.setVersion(ProtocolConstants.PROTOCOL_VERSION);

        // 测试无效的包体长度
        header.setBodyLength(-1);
        assertFalse(header.isValidBodyLength());
        assertFalse(header.isValid());

        header.setBodyLength(ProtocolConstants.MAX_BODY_LENGTH + 1);
        assertFalse(header.isValidBodyLength());
        assertFalse(header.isValid());

        // 恢复包体长度
        header.setBodyLength(100);
        assertTrue(header.isValidBodyLength());
        assertTrue(header.isValid());

        // 测试保留字段为null
        header.setReserved(null);
        assertFalse(header.isValid());

        // 恢复保留字段
        header.setReserved(new byte[ProtocolConstants.HeaderLengths.RESERVED]);
        assertTrue(header.isValid());
    }
    

    
    @Test
    void testSerialization() {
        ProtocolHeader original = new ProtocolHeader();
        original.setProtocolId(ProtocolConstants.SystemProtocols.HEARTBEAT);
        original.setEncoding(ProtocolConstants.Encoding.JSON);
        original.setEncryption(ProtocolConstants.Encryption.NONE);
        original.setBodyLength(50);
        original.setTimestamp(TimestampUtil.currentTimestamp());
        
        // 序列化
        byte[] data = original.toBytes();
        assertEquals(ProtocolConstants.HEADER_LENGTH, data.length);
        
        // 反序列化
        ProtocolHeader deserialized = ProtocolHeader.fromBytes(data);
        
        // 验证数据一致性
        assertEquals(original.getMagic(), deserialized.getMagic());
        assertEquals(original.getVersion(), deserialized.getVersion());
        assertEquals(original.getProtocolId(), deserialized.getProtocolId());
        assertEquals(original.getEncoding(), deserialized.getEncoding());
        assertEquals(original.getEncryption(), deserialized.getEncryption());
        assertEquals(original.getBodyLength(), deserialized.getBodyLength());
        assertEquals(original.getTimestamp(), deserialized.getTimestamp());
        assertArrayEquals(original.getReserved(), deserialized.getReserved());
    }
    
    @Test
    void testFromBytesWithInvalidData() {
        // 测试空数据
        assertThrows(IllegalArgumentException.class, () -> {
            ProtocolHeader.fromBytes(null);
        });
        
        // 测试数据长度不足
        byte[] shortData = new byte[ProtocolConstants.HEADER_LENGTH - 1];
        assertThrows(IllegalArgumentException.class, () -> {
            ProtocolHeader.fromBytes(shortData);
        });
    }
    
    @Test
    void testEqualsAndHashCode() {
        ProtocolHeader header1 = new ProtocolHeader();
        ProtocolHeader header2 = new ProtocolHeader();
        
        // 默认情况下应该相等
        assertEquals(header1, header2);
        assertEquals(header1.hashCode(), header2.hashCode());
        
        // 修改一个字段
        header2.setProtocolId(ProtocolConstants.SystemProtocols.HEARTBEAT);
        assertNotEquals(header1, header2);
        assertNotEquals(header1.hashCode(), header2.hashCode());
    }
    
    @Test
    void testToString() {
        ProtocolHeader header = new ProtocolHeader();
        String str = header.toString();

        assertNotNull(str);
        assertTrue(str.contains("ProtocolHeader"));
        assertTrue(str.contains("magic"));
        assertTrue(str.contains("version"));
        assertTrue(str.contains("protocolId"));
    }

    @Test
    void testToBytes() {
        ProtocolHeader header = new ProtocolHeader();
        header.setProtocolId(ProtocolConstants.SystemProtocols.HEARTBEAT);
        header.setBodyLength(50);

        byte[] bytes = header.toBytes();

        assertNotNull(bytes);
        assertEquals(ProtocolConstants.HEADER_LENGTH, bytes.length);

        // 验证可以反序列化回来
        ProtocolHeader deserialized = ProtocolHeader.fromBytes(bytes);
        assertEquals(header.getProtocolId(), deserialized.getProtocolId());
        assertEquals(header.getBodyLength(), deserialized.getBodyLength());
    }

    @Test
    void testFromBytesInvalidData() {
        // 测试null数据
        assertThrows(IllegalArgumentException.class, () -> {
            ProtocolHeader.fromBytes(null);
        });

        // 测试数据长度不足
        byte[] shortData = new byte[ProtocolConstants.HEADER_LENGTH - 1];
        assertThrows(IllegalArgumentException.class, () -> {
            ProtocolHeader.fromBytes(shortData);
        });
    }



    @Test
    void testFromByteBuffer() {
        ProtocolHeader original = new ProtocolHeader();
        original.setProtocolId(ProtocolConstants.SystemProtocols.HEARTBEAT);
        original.setEncoding(ProtocolConstants.Encoding.JSON);
        original.setEncryption(ProtocolConstants.Encryption.NONE);
        original.setBodyLength(50);

        ByteBuffer buffer = original.toByteBuffer();

        ProtocolHeader deserialized = ProtocolHeader.fromByteBuffer(buffer);

        assertEquals(original.getMagic(), deserialized.getMagic());
        assertEquals(original.getVersion(), deserialized.getVersion());
        assertEquals(original.getProtocolId(), deserialized.getProtocolId());
        assertEquals(original.getEncoding(), deserialized.getEncoding());
        assertEquals(original.getEncryption(), deserialized.getEncryption());
        assertEquals(original.getBodyLength(), deserialized.getBodyLength());
        assertEquals(original.getTimestamp(), deserialized.getTimestamp());
        assertArrayEquals(original.getReserved(), deserialized.getReserved());
    }

    @Test
    void testFromByteBufferInvalidData() {
        ByteBuffer buffer = ByteBufferUtil.allocate(10); // 长度不足

        assertThrows(IllegalArgumentException.class, () -> {
            ProtocolHeader.fromByteBuffer(buffer);
        });
    }

    @Test
    void testAllProtocolConstants() {
        // 测试所有常量值都能被正确使用
        ProtocolHeader header = new ProtocolHeader();

        // 测试所有编码格式
        header.setEncoding(ProtocolConstants.Encoding.JSON);
        assertEquals(ProtocolConstants.Encoding.JSON, header.getEncoding());

        header.setEncoding(ProtocolConstants.Encoding.YAML);
        assertEquals(ProtocolConstants.Encoding.YAML, header.getEncoding());

        header.setEncoding(ProtocolConstants.Encoding.XML);
        assertEquals(ProtocolConstants.Encoding.XML, header.getEncoding());

        header.setEncoding(ProtocolConstants.Encoding.BINARY);
        assertEquals(ProtocolConstants.Encoding.BINARY, header.getEncoding());

        // 测试所有加密方式
        header.setEncryption(ProtocolConstants.Encryption.NONE);
        assertEquals(ProtocolConstants.Encryption.NONE, header.getEncryption());

        header.setEncryption(ProtocolConstants.Encryption.DTLS);
        assertEquals(ProtocolConstants.Encryption.DTLS, header.getEncryption());

        header.setEncryption(ProtocolConstants.Encryption.AES);
        assertEquals(ProtocolConstants.Encryption.AES, header.getEncryption());
    }

    @Test
    void testProtocolIdRange() {
        ProtocolHeader header = new ProtocolHeader();

        // 测试系统协议号范围
        header.setProtocolId(ProtocolConstants.SystemProtocols.HEARTBEAT);
        assertTrue(header.getProtocolId() >= ProtocolConstants.ProtocolRanges.SYSTEM_START);
        assertTrue(header.getProtocolId() <= ProtocolConstants.ProtocolRanges.SYSTEM_END);

        // 测试业务协议号范围
        header.setProtocolId(ProtocolConstants.BusinessProtocols.USER_LOGIN);
        assertTrue(header.getProtocolId() >= ProtocolConstants.ProtocolRanges.BUSINESS_START);
        assertTrue(header.getProtocolId() <= ProtocolConstants.ProtocolRanges.BUSINESS_END);

        // 测试扩展协议号范围
        header.setProtocolId((short) 0x2000);
        assertTrue(header.getProtocolId() >= ProtocolConstants.ProtocolRanges.EXTENSION_START);
        assertTrue(header.getProtocolId() <= ProtocolConstants.ProtocolRanges.EXTENSION_END);
    }
}
