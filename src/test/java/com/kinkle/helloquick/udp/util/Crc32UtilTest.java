package com.kinkle.helloquick.udp.util;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CRC32校验工具类测试
 *
 * @author kinkle
 * @version 1.0
 * @since 2025-08-31
 */
class Crc32UtilTest {

    @Test
    void testCalculateByteArray() {
        byte[] data = "Hello, World!".getBytes();
        long crc32 = Crc32Util.calculate(data);

        assertTrue(crc32 > 0, "CRC32 should be positive for non-empty data");
        // 验证CRC32值不为0且为正数
        assertTrue(crc32 > 0 && crc32 <= 0xFFFFFFFFL);
    }

    @Test
    void testCalculateByteArrayEmpty() {
        byte[] data = new byte[0];
        long crc32 = Crc32Util.calculate(data);
        assertEquals(0, crc32);
    }

    @Test
    void testCalculateByteArrayNull() {
        long crc32 = Crc32Util.calculate((byte[]) null);
        assertEquals(0, crc32);
    }

    @Test
    void testCalculateByteArrayWithRange() {
        byte[] data = "Hello, World!".getBytes();
        long crc32 = Crc32Util.calculate(data, 7, 5); // "World"

        assertTrue(crc32 > 0);
        assertTrue(crc32 > 0 && crc32 <= 0xFFFFFFFFL);
    }

    @Test
    void testCalculateByteArrayWithRangeInvalid() {
        byte[] data = "Hello".getBytes();

        // 无效的offset
        assertEquals(0, Crc32Util.calculate(data, -1, 3));
        // 无效的length
        assertEquals(0, Crc32Util.calculate(data, 0, 0));
        // offset + length超出范围
        assertEquals(0, Crc32Util.calculate(data, 2, 10));
        // null数据
        assertEquals(0, Crc32Util.calculate((byte[]) null, 0, 5));
    }

    @Test
    void testCalculateByteBuffer() {
        ByteBuffer buffer = ByteBufferUtil.wrap("Hello, World!".getBytes());
        long crc32 = Crc32Util.calculate(buffer);

        assertTrue(crc32 > 0);
        assertTrue(crc32 > 0 && crc32 <= 0xFFFFFFFFL);
    }

    @Test
    void testCalculateByteBufferEmpty() {
        ByteBuffer buffer = ByteBufferUtil.allocate(0);
        long crc32 = Crc32Util.calculate(buffer);
        assertEquals(0, crc32);
    }

    @Test
    void testCalculateByteBufferNull() {
        long crc32 = Crc32Util.calculate((ByteBuffer) null);
        assertEquals(0, crc32);
    }

    @Test
    void testCalculateByteBufferWithRange() {
        ByteBuffer buffer = ByteBufferUtil.allocate(20);
        byte[] data = "Hello, World!".getBytes();
        buffer.put(data);
        buffer.flip();

        long crc32 = Crc32Util.calculate(buffer, 7, 5); // "World"
        assertTrue(crc32 > 0);
        assertTrue(crc32 > 0 && crc32 <= 0xFFFFFFFFL);
    }

    @Test
    void testCalculateByteBufferWithRangeInvalid() {
        ByteBuffer buffer = ByteBufferUtil.allocate(10);

        // 无效的offset
        assertEquals(0, Crc32Util.calculate(buffer, -1, 5));
        // 无效的length
        assertEquals(0, Crc32Util.calculate(buffer, 0, 0));
        // offset + length超出范围
        assertEquals(0, Crc32Util.calculate(buffer, 5, 10));
        // null buffer
        assertEquals(0, Crc32Util.calculate((ByteBuffer) null, 0, 5));
    }

    @Test
    void testCalculateWithHeaderAndBody() {
        byte[] header = "Header".getBytes();
        byte[] body = "Body".getBytes();

        long crc32 = Crc32Util.calculate(header, body);
        assertTrue(crc32 > 0);

        // 验证与单独计算的结果一致 - 使用辅助方法连接字节数组
        byte[] combined = combineByteArrays(header, body);
        long expectedCrc32 = Crc32Util.calculate(combined);
        assertEquals(expectedCrc32, crc32);
    }

    /**
     * 辅助方法：连接两个字节数组
     */
    private byte[] combineByteArrays(byte[] array1, byte[] array2) {
        if (array1 == null && array2 == null) {
            return new byte[0];
        }
        if (array1 == null) {
            return array2.clone();
        }
        if (array2 == null) {
            return array1.clone();
        }

        byte[] result = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    @Test
    void testCalculateWithHeaderAndBodyNull() {
        // header为null
        byte[] body = "Body".getBytes();
        long crc32 = Crc32Util.calculate(null, body);
        assertEquals(Crc32Util.calculate(body), crc32);

        // body为null
        byte[] header = "Header".getBytes();
        crc32 = Crc32Util.calculate(header, null);
        assertEquals(Crc32Util.calculate(header), crc32);

        // 都为null
        crc32 = Crc32Util.calculate(null, null);
        assertEquals(0, crc32);
    }

    @Test
    void testVerifyByteArray() {
        byte[] data = "Hello, World!".getBytes();
        long expectedCrc32 = Crc32Util.calculate(data);

        assertTrue(Crc32Util.verify(data, expectedCrc32));
        assertFalse(Crc32Util.verify(data, expectedCrc32 + 1));
    }

    @Test
    void testVerifyByteArrayWithRange() {
        byte[] data = "Hello, World!".getBytes();
        long expectedCrc32 = Crc32Util.calculate(data, 7, 5);

        assertTrue(Crc32Util.verify(data, 7, 5, expectedCrc32));
        assertFalse(Crc32Util.verify(data, 7, 5, expectedCrc32 + 1));
    }

    @Test
    void testVerifyByteBuffer() {
        ByteBuffer buffer = ByteBufferUtil.wrap("Hello, World!".getBytes());
        long expectedCrc32 = Crc32Util.calculate(buffer);

        // 重置buffer位置
        buffer.flip();
        assertTrue(Crc32Util.verify(buffer, expectedCrc32));

        buffer.flip();
        assertFalse(Crc32Util.verify(buffer, expectedCrc32 + 1));
    }

    @Test
    void testVerifyWithHeaderAndBody() {
        byte[] header = "Header".getBytes();
        byte[] body = "Body".getBytes();
        long expectedCrc32 = Crc32Util.calculate(header, body);

        assertTrue(Crc32Util.verify(header, body, expectedCrc32));
        assertFalse(Crc32Util.verify(header, body, expectedCrc32 + 1));
    }

    @Test
    void testToBytes() {
        long crc32 = 0x12345678L;
        byte[] bytes = Crc32Util.toBytes(crc32);

        assertNotNull(bytes);
        assertEquals(4, bytes.length);
        assertArrayEquals(new byte[]{0x12, 0x34, 0x56, 0x78}, bytes);
    }

    @Test
    void testFromBytes() {
        byte[] data = {0x12, 0x34, 0x56, 0x78, 0x00, 0x00};
        long crc32 = Crc32Util.fromBytes(data, 0);

        assertEquals(0x12345678L, crc32);
    }

    @Test
    void testFromBytesInvalid() {
        byte[] data = {0x12, 0x34};

        // 无效的offset
        assertEquals(0, Crc32Util.fromBytes(data, -1));
        // 数据长度不足
        assertEquals(0, Crc32Util.fromBytes(data, 0));
        // offset + 4超出范围
        assertEquals(0, Crc32Util.fromBytes(data, 1));
        // null数据
        assertEquals(0, Crc32Util.fromBytes(null, 0));
    }

    @Test
    void testCalculateAsBytes() {
        byte[] data = "Hello, World!".getBytes();
        byte[] crcBytes = Crc32Util.calculateAsBytes(data);

        assertNotNull(crcBytes);
        assertEquals(4, crcBytes.length);

        // 验证与toBytes的结果一致
        long crc32 = Crc32Util.calculate(data);
        byte[] expectedBytes = Crc32Util.toBytes(crc32);
        assertArrayEquals(expectedBytes, crcBytes);
    }

    @Test
    void testCalculateAsBytesWithRange() {
        byte[] data = "Hello, World!".getBytes();
        byte[] crcBytes = Crc32Util.calculateAsBytes(data, 7, 5);

        assertNotNull(crcBytes);
        assertEquals(4, crcBytes.length);

        // 验证与toBytes的结果一致
        long crc32 = Crc32Util.calculate(data, 7, 5);
        byte[] expectedBytes = Crc32Util.toBytes(crc32);
        assertArrayEquals(expectedBytes, crcBytes);
    }

    @Test
    void testCrc32Consistency() {
        byte[] data = "Test data for CRC32".getBytes();

        // 验证不同方法计算的CRC32一致性
        long crc1 = Crc32Util.calculate(data);
        long crc2 = Crc32Util.calculate(data, 0, data.length);

        ByteBuffer buffer = ByteBufferUtil.wrap(data);
        long crc3 = Crc32Util.calculate(buffer);

        assertEquals(crc1, crc2);
        assertEquals(crc1, crc3);
    }

    @Test
    void testCrc32WithDifferentData() {
        byte[] data1 = "Hello".getBytes();
        byte[] data2 = "World".getBytes();

        long crc1 = Crc32Util.calculate(data1);
        long crc2 = Crc32Util.calculate(data2);

        assertNotEquals(crc1, crc2, "Different data should have different CRC32 values");
    }

    @Test
    void testConstructorThrowsException() {
        assertThrows(IllegalAccessException.class, () -> {
            // 尝试通过反射调用私有构造函数 - 由于安全限制会抛出IllegalAccessException
            Crc32Util.class.getDeclaredConstructor().setAccessible(true);
            Crc32Util.class.getDeclaredConstructor().newInstance();
        });
    }
}
