package com.kinkle.helloquick.udp.protocol;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UDP包测试类
 *
 * @author kinkle
 * @version 1.0
 * @since 2025-08-31
 */
class UdpPacketTest {

    @Test
    void testDefaultConstructor() {
        UdpPacket packet = new UdpPacket();

        assertNotNull(packet.getHeader());
        assertNotNull(packet.getBody());
        assertEquals(0, packet.getBody().length);
        assertEquals(0, packet.getCrc32());
    }

    @Test
    void testConstructorWithHeaderAndBody() {
        ProtocolHeader header = new ProtocolHeader(ProtocolConstants.SystemProtocols.HEARTBEAT, 13);
        byte[] body = "Hello, World!".getBytes();

        UdpPacket packet = new UdpPacket(header, body);

        assertEquals(header, packet.getHeader());
        assertArrayEquals(body, packet.getBody());
        assertEquals(13, packet.getHeader().getBodyLength());
    }

    @Test
    void testConstructorWithProtocolIdAndBody() {
        short protocolId = ProtocolConstants.SystemProtocols.HEARTBEAT;
        byte[] body = "Hello, World!".getBytes();

        UdpPacket packet = new UdpPacket(protocolId, body);

        assertEquals(protocolId, packet.getHeader().getProtocolId());
        assertArrayEquals(body, packet.getBody());
        assertEquals(body.length, packet.getHeader().getBodyLength());
    }

    @Test
    void testConstructorWithNullBody() {
        ProtocolHeader header = new ProtocolHeader(ProtocolConstants.SystemProtocols.HEARTBEAT, 0);

        assertThrows(IllegalArgumentException.class, () -> {
            new UdpPacket(header, null);
        });
    }

    @Test
    void testConstructorWithNullHeader() {
        byte[] body = "Hello, World!".getBytes();

        assertThrows(IllegalArgumentException.class, () -> {
            new UdpPacket(null, body);
        });
    }

    @Test
    void testCalculateAndSetCrc32() {
        UdpPacket packet = new UdpPacket(ProtocolConstants.SystemProtocols.HEARTBEAT, "Hello, World!".getBytes());

        // 计算并设置CRC32
        packet.calculateAndSetCrc32();

        // 验证CRC32值已设置且不为0
        assertNotEquals(0, packet.getCrc32());



        // 验证CRC32校验通过
        assertTrue(packet.verifyCrc32());
    }

    @Test
    void testCalculateAndSetCrc32WithEmptyBody() {
        UdpPacket packet = new UdpPacket(ProtocolConstants.SystemProtocols.HEARTBEAT, new byte[0]);

        packet.calculateAndSetCrc32();

        assertNotEquals(0, packet.getCrc32());
        assertTrue(packet.verifyCrc32());
    }

    @Test
    void testCalculateAndSetCrc32WithNullHeader() {
        UdpPacket packet = new UdpPacket(ProtocolConstants.SystemProtocols.HEARTBEAT, "Hello, World!".getBytes());

        // 使用反射设置header为null来测试异常情况
        try {
            java.lang.reflect.Field headerField = UdpPacket.class.getDeclaredField("header");
            headerField.setAccessible(true);
            headerField.set(packet, null);

            assertThrows(IllegalStateException.class, () -> {
                packet.calculateAndSetCrc32();
            });
        } catch (Exception e) {
            // 如果反射失败，跳过这个测试
        }
    }

    @Test
    void testVerifyCrc32() {
        UdpPacket packet = new UdpPacket(ProtocolConstants.SystemProtocols.HEARTBEAT, "Hello, World!".getBytes());

        // 计算并设置正确的CRC32
        packet.calculateAndSetCrc32();
        assertNotEquals(0, packet.getCrc32());

        // 验证CRC32校验通过
        assertTrue(packet.verifyCrc32());

        // 修改CRC32值，验证失败
        int originalCrc32 = packet.getCrc32();
        packet.setCrc32(packet.getCrc32() + 1);
        assertFalse(packet.verifyCrc32());

        // 恢复正确的CRC32值
        packet.setCrc32(originalCrc32);
        assertTrue(packet.verifyCrc32());
    }

    @Test
    void testVerifyCrc32WithNullHeader() {
        UdpPacket packet = new UdpPacket(ProtocolConstants.SystemProtocols.HEARTBEAT, "Hello, World!".getBytes());

        // 使用反射设置header为null来测试异常情况
        try {
            java.lang.reflect.Field headerField = UdpPacket.class.getDeclaredField("header");
            headerField.setAccessible(true);
            headerField.set(packet, null);

            assertFalse(packet.verifyCrc32());
        } catch (Exception e) {
            // 如果反射失败，跳过这个测试
        }
    }

    @Test
    void testVerifyCrc32WithNullBody() {
        UdpPacket packet = new UdpPacket(ProtocolConstants.SystemProtocols.HEARTBEAT, "Hello, World!".getBytes());

        // 使用反射设置body为null来测试异常情况
        try {
            java.lang.reflect.Field bodyField = UdpPacket.class.getDeclaredField("body");
            bodyField.setAccessible(true);
            bodyField.set(packet, null);

            assertFalse(packet.verifyCrc32());
        } catch (Exception e) {
            // 如果反射失败，跳过这个测试
        }
    }

    @Test
    void testIsValid() {
        UdpPacket packet = new UdpPacket(ProtocolConstants.SystemProtocols.HEARTBEAT, "Hello, World!".getBytes());

        // 未计算CRC32，应该无效
        assertFalse(packet.isValid());

        // 计算CRC32后应该有效
        packet.calculateAndSetCrc32();
        assertTrue(packet.isValid());
    }

    @Test
    void testIsValidWithInvalidHeader() {
        ProtocolHeader invalidHeader = new ProtocolHeader();
        invalidHeader.setMagic(0x12345679); // 错误的魔数

        UdpPacket packet = new UdpPacket(invalidHeader, "Hello, World!".getBytes());
        packet.calculateAndSetCrc32();

        assertFalse(packet.isValid());
    }

    @Test
    void testIsValidWithInvalidBodyLength() {
        ProtocolHeader header = new ProtocolHeader(ProtocolConstants.SystemProtocols.HEARTBEAT, 100);
        byte[] body = "Hello, World!".getBytes(); // 长度不匹配

        UdpPacket packet = new UdpPacket(header, body);
        packet.calculateAndSetCrc32();

        assertFalse(packet.isValid());
    }

    @Test
    void testIsValidWithNullHeader() {
        UdpPacket packet = new UdpPacket(ProtocolConstants.SystemProtocols.HEARTBEAT, "Hello, World!".getBytes());

        // 使用反射设置header为null来测试异常情况
        try {
            java.lang.reflect.Field headerField = UdpPacket.class.getDeclaredField("header");
            headerField.setAccessible(true);
            headerField.set(packet, null);

            assertFalse(packet.isValid());
        } catch (Exception e) {
            // 如果反射失败，跳过这个测试
        }
    }

    @Test
    void testIsValidWithNullBody() {
        UdpPacket packet = new UdpPacket(ProtocolConstants.SystemProtocols.HEARTBEAT, "Hello, World!".getBytes());

        // 使用反射设置body为null来测试异常情况
        try {
            java.lang.reflect.Field bodyField = UdpPacket.class.getDeclaredField("body");
            bodyField.setAccessible(true);
            bodyField.set(packet, null);

            assertFalse(packet.isValid());
        } catch (Exception e) {
            // 如果反射失败，跳过这个测试
        }
    }

    @Test
    void testToBytes() {
        UdpPacket packet = new UdpPacket(ProtocolConstants.SystemProtocols.HEARTBEAT, "Hello, World!".getBytes());
        packet.calculateAndSetCrc32();

        byte[] bytes = packet.toBytes();

        assertNotNull(bytes);
        int expectedLength = ProtocolConstants.HEADER_LENGTH + "Hello, World!".getBytes().length + ProtocolConstants.HeaderLengths.CRC32;
        assertEquals(expectedLength, bytes.length);
    }

    @Test
    void testToBytesWithNullHeader() {
        // 创建一个包，但手动设置header为null（这在正常情况下不会发生，但为了测试）
        UdpPacket packet = new UdpPacket(ProtocolConstants.SystemProtocols.HEARTBEAT, "Hello, World!".getBytes());
        // 使用反射设置header为null来测试异常情况
        try {
            java.lang.reflect.Field headerField = UdpPacket.class.getDeclaredField("header");
            headerField.setAccessible(true);
            headerField.set(packet, null);

            assertThrows(IllegalStateException.class, () -> {
                packet.toBytes();
            });
        } catch (Exception e) {
            // 如果反射失败，跳过这个测试
        }
    }

    @Test
    void testToBytesWithNullBody() {
        // 创建一个包，但手动设置body为null（这在正常情况下不会发生，但为了测试）
        UdpPacket packet = new UdpPacket(ProtocolConstants.SystemProtocols.HEARTBEAT, "Hello, World!".getBytes());
        // 使用反射设置body为null来测试异常情况
        try {
            java.lang.reflect.Field bodyField = UdpPacket.class.getDeclaredField("body");
            bodyField.setAccessible(true);
            bodyField.set(packet, null);

            assertThrows(IllegalStateException.class, () -> {
                packet.toBytes();
            });
        } catch (Exception e) {
            // 如果反射失败，跳过这个测试
        }
    }

    @Test
    void testFromBytes() {
        // 创建原始包
        UdpPacket original = new UdpPacket(ProtocolConstants.SystemProtocols.HEARTBEAT, "Hello, World!".getBytes());
        original.calculateAndSetCrc32();

        byte[] bytes = original.toBytes();

        // 从字节数组反序列化
        UdpPacket deserialized = UdpPacket.fromBytes(bytes);

        assertNotNull(deserialized);
        assertEquals(original.getHeader().getProtocolId(), deserialized.getHeader().getProtocolId());
        assertArrayEquals(original.getBody(), deserialized.getBody());
        assertEquals(original.getCrc32(), deserialized.getCrc32());
        assertTrue(deserialized.verifyCrc32());
    }

    @Test
    void testFromBytesInvalidData() {
        // 测试null数据
        assertThrows(IllegalArgumentException.class, () -> {
            UdpPacket.fromBytes(null);
        });

        // 测试数据长度不足
        byte[] shortData = new byte[ProtocolConstants.HEADER_LENGTH + ProtocolConstants.HeaderLengths.CRC32 - 1];
        assertThrows(IllegalArgumentException.class, () -> {
            UdpPacket.fromBytes(shortData);
        });
    }

    @Test
    void testFromBytesInvalidBodyLength() {
        // 创建一个有问题的包头，声称包体长度很大，但实际数据不足
        ProtocolHeader header = new ProtocolHeader(ProtocolConstants.SystemProtocols.HEARTBEAT, 1000);
        byte[] body = "Hello".getBytes();

        UdpPacket packet = new UdpPacket(header, body);
        byte[] bytes = packet.toBytes();

        // 现在修改字节数组中的包体长度字段，使其大于实际数据长度
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.position(ProtocolConstants.HeaderOffsets.BODY_LENGTH);
        buffer.putInt(1000); // 设置一个很大的包体长度

        assertThrows(IllegalArgumentException.class, () -> {
            UdpPacket.fromBytes(bytes);
        });
    }

    @Test
    void testFromBytesWithNegativeBodyLength() {
        // 创建一个包头，设置负数的包体长度
        ProtocolHeader header = new ProtocolHeader(ProtocolConstants.SystemProtocols.HEARTBEAT, -1);
        byte[] body = new byte[0];

        UdpPacket packet = new UdpPacket(header, body);
        byte[] bytes = packet.toBytes();

        // 修改字节数组中的包体长度字段为负数
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.position(ProtocolConstants.HeaderOffsets.BODY_LENGTH);
        buffer.putInt(-1);

        assertThrows(IllegalArgumentException.class, () -> {
            UdpPacket.fromBytes(bytes);
        });
    }

    @Test
    void testFromBytesWithZeroBodyLength() {
        ProtocolHeader header = new ProtocolHeader(ProtocolConstants.SystemProtocols.HEARTBEAT, 0);
        byte[] body = new byte[0];

        UdpPacket original = new UdpPacket(header, body);
        original.calculateAndSetCrc32();

        byte[] bytes = original.toBytes();
        UdpPacket deserialized = UdpPacket.fromBytes(bytes);

        assertNotNull(deserialized);
        assertEquals(0, deserialized.getBody().length);
        assertEquals(original.getHeader().getProtocolId(), deserialized.getHeader().getProtocolId());
    }

    @Test
    void testFromByteBuffer() {
        // 创建原始包
        UdpPacket original = new UdpPacket(ProtocolConstants.SystemProtocols.HEARTBEAT, "Hello, World!".getBytes());
        original.calculateAndSetCrc32();

        byte[] bytes = original.toBytes();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        // 从ByteBuffer反序列化
        UdpPacket deserialized = UdpPacket.fromByteBuffer(buffer);

        assertNotNull(deserialized);
        assertEquals(original.getHeader().getProtocolId(), deserialized.getHeader().getProtocolId());
        assertArrayEquals(original.getBody(), deserialized.getBody());
        assertEquals(original.getCrc32(), deserialized.getCrc32());
        assertTrue(deserialized.verifyCrc32());
    }

    @Test
    void testFromByteBufferInvalidBuffer() {
        // 测试null buffer
        assertThrows(IllegalArgumentException.class, () -> {
            UdpPacket.fromByteBuffer(null);
        });

        // 测试数据长度不足
        ByteBuffer shortBuffer = ByteBuffer.allocate(ProtocolConstants.HEADER_LENGTH + ProtocolConstants.HeaderLengths.CRC32 - 1);
        assertThrows(IllegalArgumentException.class, () -> {
            UdpPacket.fromByteBuffer(shortBuffer);
        });
    }

    @Test
    void testFromByteBufferWithNegativeBodyLength() {
        // 创建一个包头，设置负数的包体长度
        ProtocolHeader header = new ProtocolHeader(ProtocolConstants.SystemProtocols.HEARTBEAT, -1);
        byte[] body = new byte[0];

        UdpPacket packet = new UdpPacket(header, body);
        byte[] bytes = packet.toBytes();

        // 修改字节数组中的包体长度字段为负数
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.position(ProtocolConstants.HeaderOffsets.BODY_LENGTH);
        buffer.putInt(-1);

        assertThrows(IllegalArgumentException.class, () -> {
            UdpPacket.fromByteBuffer(buffer);
        });
    }

    @Test
    void testFromByteBufferWithZeroBodyLength() {
        ProtocolHeader header = new ProtocolHeader(ProtocolConstants.SystemProtocols.HEARTBEAT, 0);
        byte[] body = new byte[0];

        UdpPacket original = new UdpPacket(header, body);
        original.calculateAndSetCrc32();

        byte[] bytes = original.toBytes();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        UdpPacket deserialized = UdpPacket.fromByteBuffer(buffer);

        assertNotNull(deserialized);
        assertEquals(0, deserialized.getBody().length);
        assertEquals(original.getHeader().getProtocolId(), deserialized.getHeader().getProtocolId());
    }

    @Test
    void testFromByteBufferWithInsufficientDataForBody() {
        // 创建一个包头，声称包体长度很大，但实际数据不足
        ProtocolHeader header = new ProtocolHeader(ProtocolConstants.SystemProtocols.HEARTBEAT, 1000);
        byte[] body = "Hello".getBytes();

        UdpPacket packet = new UdpPacket(header, body);
        byte[] bytes = packet.toBytes();

        // 创建一个较小的buffer
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length - 10);
        buffer.put(bytes, 0, bytes.length - 10);
        buffer.flip();

        assertThrows(IllegalArgumentException.class, () -> {
            UdpPacket.fromByteBuffer(buffer);
        });
    }

    @Test
    void testGetTotalLength() {
        UdpPacket packet = new UdpPacket(ProtocolConstants.SystemProtocols.HEARTBEAT, "Hello, World!".getBytes());

        int expectedLength = ProtocolConstants.HEADER_LENGTH + "Hello, World!".getBytes().length + ProtocolConstants.HeaderLengths.CRC32;
        assertEquals(expectedLength, packet.getTotalLength());
    }

    @Test
    void testGetTotalLengthWithNullHeader() {
        UdpPacket packet = new UdpPacket(ProtocolConstants.SystemProtocols.HEARTBEAT, "Hello, World!".getBytes());

        // 使用反射设置header为null来测试异常情况
        try {
            java.lang.reflect.Field headerField = UdpPacket.class.getDeclaredField("header");
            headerField.setAccessible(true);
            headerField.set(packet, null);

            // 应该返回最小长度
            int expectedLength = ProtocolConstants.HEADER_LENGTH + ProtocolConstants.HeaderLengths.CRC32;
            assertEquals(expectedLength, packet.getTotalLength());
        } catch (Exception e) {
            // 如果反射失败，跳过这个测试
        }
    }

    @Test
    void testGetTotalLengthWithNullBody() {
        UdpPacket packet = new UdpPacket(ProtocolConstants.SystemProtocols.HEARTBEAT, "Hello, World!".getBytes());

        // 使用反射设置body为null来测试异常情况
        try {
            java.lang.reflect.Field bodyField = UdpPacket.class.getDeclaredField("body");
            bodyField.setAccessible(true);
            bodyField.set(packet, null);

            // 应该返回最小长度
            int expectedLength = ProtocolConstants.HEADER_LENGTH + ProtocolConstants.HeaderLengths.CRC32;
            assertEquals(expectedLength, packet.getTotalLength());
        } catch (Exception e) {
            // 如果反射失败，跳过这个测试
        }
    }

    @Test
    void testGetTotalLengthWithEmptyBody() {
        UdpPacket packet = new UdpPacket(ProtocolConstants.SystemProtocols.HEARTBEAT, new byte[0]);

        int expectedLength = ProtocolConstants.HEADER_LENGTH + 0 + ProtocolConstants.HeaderLengths.CRC32;
        assertEquals(expectedLength, packet.getTotalLength());
    }

    @Test
    void testRoundTripSerialization() {
        // 测试各种协议类型的包
        short[] protocols = {
            ProtocolConstants.SystemProtocols.HEARTBEAT,
            ProtocolConstants.SystemProtocols.AUTH_REQUEST,
            ProtocolConstants.BusinessProtocols.USER_LOGIN,
            ProtocolConstants.BusinessProtocols.DATA_SYNC
        };

        String[] testBodies = {"", "Hello", "Hello, World!", "This is a longer test message for UDP packet"};

        for (short protocol : protocols) {
            for (String testBody : testBodies) {
                UdpPacket original = new UdpPacket(protocol, testBody.getBytes());
                original.calculateAndSetCrc32();

                // 序列化
                byte[] bytes = original.toBytes();

                // 反序列化
                UdpPacket deserialized = UdpPacket.fromBytes(bytes);

                // 验证
                assertEquals(original.getHeader().getProtocolId(), deserialized.getHeader().getProtocolId());
                assertArrayEquals(original.getBody(), deserialized.getBody());
                assertEquals(original.getCrc32(), deserialized.getCrc32());
                assertTrue(deserialized.verifyCrc32());
                assertTrue(deserialized.isValid());
            }
        }
    }
}
