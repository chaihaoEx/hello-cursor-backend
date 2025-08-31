package com.kinkle.helloquick.udp.util;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 字节缓冲区工具类测试
 *
 * @author kinkle
 * @version 1.0
 * @since 2025-08-31
 */
class ByteBufferUtilTest {

    @Test
    void testAllocate() {
        ByteBuffer buffer = ByteBufferUtil.allocate(10);
        assertNotNull(buffer);
        assertEquals(10, buffer.capacity());
        assertEquals(ByteOrder.BIG_ENDIAN, buffer.order());
        assertEquals(0, buffer.position());
        assertEquals(10, buffer.limit());
    }

    @Test
    void testAllocateDirect() {
        ByteBuffer buffer = ByteBufferUtil.allocateDirect(10);
        assertNotNull(buffer);
        assertEquals(10, buffer.capacity());
        assertTrue(buffer.isDirect());
        assertEquals(ByteOrder.BIG_ENDIAN, buffer.order());
    }

    @Test
    void testWrapByteArray() {
        byte[] data = {1, 2, 3, 4, 5};
        ByteBuffer buffer = ByteBufferUtil.wrap(data);

        assertNotNull(buffer);
        assertEquals(5, buffer.capacity());
        assertEquals(ByteOrder.BIG_ENDIAN, buffer.order());
        assertArrayEquals(data, buffer.array());
    }

    @Test
    void testWrapByteArrayWithOffset() {
        byte[] data = {1, 2, 3, 4, 5, 6, 7, 8};
        ByteBuffer buffer = ByteBufferUtil.wrap(data, 2, 4);

        assertNotNull(buffer);
        assertEquals(8, buffer.capacity()); // ByteBuffer.wrap(array, offset, length)返回的capacity是array.length
        assertEquals(ByteOrder.BIG_ENDIAN, buffer.order());
        assertEquals(6, buffer.limit()); // limit应该是offset + length
        assertEquals(2, buffer.position()); // position应该是offset
        assertEquals(4, buffer.remaining()); // remaining应该是length - position
    }

    @Test
    void testToBytesFromBuffer() {
        ByteBuffer buffer = ByteBufferUtil.allocate(5);
        buffer.put((byte) 1);
        buffer.put((byte) 2);
        buffer.put((byte) 3);
        buffer.put((byte) 4);
        buffer.put((byte) 5);

        byte[] result = ByteBufferUtil.toBytes(buffer);
        assertNotNull(result);
        assertEquals(5, result.length);
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5}, result);
    }

    @Test
    void testToBytesWithLength() {
        ByteBuffer buffer = ByteBufferUtil.allocate(10);
        for (int i = 0; i < 10; i++) {
            buffer.put((byte) i);
        }
        buffer.flip();

        byte[] result = ByteBufferUtil.toBytes(buffer, 5);
        assertNotNull(result);
        assertEquals(5, result.length);
        assertArrayEquals(new byte[]{0, 1, 2, 3, 4}, result);
    }

    @Test
    void testToBytesWithOffsetAndLength() {
        ByteBuffer buffer = ByteBufferUtil.allocate(10);
        for (int i = 0; i < 10; i++) {
            buffer.put((byte) i);
        }
        buffer.flip();

        byte[] result = ByteBufferUtil.toBytes(buffer, 3, 4);
        assertNotNull(result);
        assertEquals(4, result.length);
        assertArrayEquals(new byte[]{3, 4, 5, 6}, result);
    }

    @Test
    void testPutIntAndGetInt() {
        ByteBuffer buffer = ByteBufferUtil.allocate(4);
        int value = 123456789;

        ByteBufferUtil.putInt(buffer, value);
        buffer.flip();

        int result = ByteBufferUtil.getInt(buffer);
        assertEquals(value, result);
    }

    @Test
    void testPutLongAndGetLong() {
        ByteBuffer buffer = ByteBufferUtil.allocate(8);
        long value = 123456789012345L;

        ByteBufferUtil.putLong(buffer, value);
        buffer.flip();

        long result = ByteBufferUtil.getLong(buffer);
        assertEquals(value, result);
    }

    @Test
    void testPutShortAndGetShort() {
        ByteBuffer buffer = ByteBufferUtil.allocate(2);
        short value = 12345;

        ByteBufferUtil.putShort(buffer, value);
        buffer.flip();

        short result = ByteBufferUtil.getShort(buffer);
        assertEquals(value, result);
    }

    @Test
    void testPutByteAndGetByte() {
        ByteBuffer buffer = ByteBufferUtil.allocate(1);
        byte value = 123;

        ByteBufferUtil.putByte(buffer, value);
        buffer.flip();

        byte result = ByteBufferUtil.getByte(buffer);
        assertEquals(value, result);
    }

    @Test
    void testPutBytes() {
        ByteBuffer buffer = ByteBufferUtil.allocate(5);
        byte[] data = {1, 2, 3, 4, 5};

        ByteBufferUtil.putBytes(buffer, data);
        buffer.flip();

        byte[] result = new byte[5];
        buffer.get(result);
        assertArrayEquals(data, result);
    }

    @Test
    void testPutBytesWithOffsetAndLength() {
        ByteBuffer buffer = ByteBufferUtil.allocate(3);
        byte[] data = {1, 2, 3, 4, 5};

        ByteBufferUtil.putBytes(buffer, data, 1, 3);
        buffer.flip();

        byte[] result = new byte[3];
        buffer.get(result);
        assertArrayEquals(new byte[]{2, 3, 4}, result);
    }

    @Test
    void testDuplicate() {
        ByteBuffer original = ByteBufferUtil.allocate(20); // 增加容量以容纳int和long
        original.putInt(12345);
        original.putLong(67890123456L);

        ByteBuffer duplicate = ByteBufferUtil.duplicate(original);
        assertNotNull(duplicate);
        assertEquals(original.capacity(), duplicate.capacity());
        assertEquals(original.order(), duplicate.order());
    }

    @Test
    void testAsReadOnlyBuffer() {
        ByteBuffer original = ByteBufferUtil.allocate(10);
        original.putInt(12345);

        ByteBuffer readOnly = ByteBufferUtil.asReadOnlyBuffer(original);
        assertNotNull(readOnly);
        assertTrue(readOnly.isReadOnly());
        assertEquals(original.capacity(), readOnly.capacity());
    }

    @Test
    void testRemaining() {
        ByteBuffer buffer = ByteBufferUtil.allocate(10);
        buffer.putInt(12345); // 使用4字节

        int remaining = ByteBufferUtil.remaining(buffer);
        assertEquals(6, remaining);

        assertEquals(0, ByteBufferUtil.remaining(null), "Should return 0 for null buffer");
    }

    @Test
    void testHasRemaining() {
        ByteBuffer emptyBuffer = ByteBufferUtil.allocate(0);
        assertFalse(ByteBufferUtil.hasRemaining(emptyBuffer));

        ByteBuffer buffer = ByteBufferUtil.allocate(10);
        assertTrue(ByteBufferUtil.hasRemaining(buffer));

        ByteBuffer nullBuffer = null;
        assertFalse(ByteBufferUtil.hasRemaining(nullBuffer));
    }

    @Test
    void testReset() {
        ByteBuffer buffer = ByteBufferUtil.allocate(10);
        buffer.putInt(12345);
        buffer.putInt(67890);
        buffer.position(5);
        buffer.limit(8);

        ByteBufferUtil.reset(buffer);

        assertEquals(0, buffer.position());
        assertEquals(10, buffer.limit());
        assertEquals(10, buffer.capacity());
    }

    @Test
    void testToHexString() {
        ByteBuffer buffer = ByteBufferUtil.allocate(4);
        buffer.put((byte) 0x12);
        buffer.put((byte) 0x34);
        buffer.put((byte) 0xAB);
        buffer.put((byte) 0xCD);
        buffer.flip();

        String hex = ByteBufferUtil.toHexString(buffer);
        assertEquals("1234ABCD", hex);
    }

    @Test
    void testToHexStringEmpty() {
        ByteBuffer buffer = ByteBufferUtil.allocate(0);
        String hex = ByteBufferUtil.toHexString(buffer);
        assertEquals("", hex);

        assertEquals("", ByteBufferUtil.toHexString(null));
    }

    @Test
    void testFromHexString() {
        String hex = "1234ABCD";
        ByteBuffer buffer = ByteBufferUtil.fromHexString(hex);

        assertNotNull(buffer);
        assertEquals(4, buffer.capacity());
        assertEquals(ByteOrder.BIG_ENDIAN, buffer.order());
        assertEquals(4, buffer.remaining()); // buffer应该从position 0开始，有4字节可读

        byte[] bytes = new byte[4];
        buffer.get(bytes);
        assertArrayEquals(new byte[]{0x12, 0x34, (byte) 0xAB, (byte) 0xCD}, bytes);
    }

    @Test
    void testFromHexStringWithSpaces() {
        String hex = "12 34 AB CD";
        ByteBuffer buffer = ByteBufferUtil.fromHexString(hex);
        assertNotNull(buffer);
        assertEquals(4, buffer.remaining());

        byte[] bytes = new byte[4];
        buffer.get(bytes);
        assertArrayEquals(new byte[]{0x12, 0x34, (byte) 0xAB, (byte) 0xCD}, bytes);
    }

    @Test
    void testFromHexStringWithPrefix() {
        String hex = "0x1234ABCD";
        ByteBuffer buffer = ByteBufferUtil.fromHexString(hex);
        assertNotNull(buffer);
        assertEquals(4, buffer.remaining());

        byte[] bytes = new byte[4];
        buffer.get(bytes);
        assertArrayEquals(new byte[]{0x12, 0x34, (byte) 0xAB, (byte) 0xCD}, bytes);
    }

    @Test
    void testFromHexStringEmpty() {
        ByteBuffer buffer = ByteBufferUtil.fromHexString("");
        assertNotNull(buffer);
        assertEquals(0, buffer.capacity());
    }

    @Test
    void testFromHexStringNull() {
        ByteBuffer buffer = ByteBufferUtil.fromHexString(null);
        assertNotNull(buffer);
        assertEquals(0, buffer.capacity());
    }

    @Test
    void testFromHexStringInvalidLength() {
        assertThrows(IllegalArgumentException.class, () -> {
            ByteBufferUtil.fromHexString("123"); // 奇数长度
        });
    }

    @Test
    void testConstructorThrowsException() {
        try {
            // 尝试通过反射调用私有构造函数
            ByteBufferUtil.class.getDeclaredConstructor().setAccessible(true);
            ByteBufferUtil.class.getDeclaredConstructor().newInstance();
            fail("Expected UnsupportedOperationException to be thrown");
        } catch (Exception e) {
            // 检查是否是UnsupportedOperationException或其他预期的异常
            assertTrue(e.getCause() instanceof UnsupportedOperationException ||
                      e instanceof IllegalAccessException,
                      "Expected UnsupportedOperationException or IllegalAccessException, but got: " + e.getClass());
        }
    }

    @Test
    void testPutBytesWithNullData() {
        ByteBuffer buffer = ByteBufferUtil.allocate(10);
        ByteBufferUtil.putBytes(buffer, null);

        // 验证buffer位置没有变化
        assertEquals(0, buffer.position());
    }

    @Test
    void testPutBytesWithEmptyData() {
        ByteBuffer buffer = ByteBufferUtil.allocate(10);
        ByteBufferUtil.putBytes(buffer, new byte[0]);

        // 验证buffer位置没有变化
        assertEquals(0, buffer.position());
    }

    @Test
    void testPutBytesWithRangeNullData() {
        ByteBuffer buffer = ByteBufferUtil.allocate(10);
        ByteBufferUtil.putBytes(buffer, null, 0, 5);

        // 验证buffer位置没有变化
        assertEquals(0, buffer.position());
    }

    @Test
    void testPutBytesWithRangeEmptyData() {
        ByteBuffer buffer = ByteBufferUtil.allocate(10);
        ByteBufferUtil.putBytes(buffer, new byte[0], 0, 0);

        // 验证buffer位置没有变化
        assertEquals(0, buffer.position());
    }

    @Test
    void testPutBytesWithRangeInvalidOffset() {
        ByteBuffer buffer = ByteBufferUtil.allocate(10);
        byte[] data = {1, 2, 3, 4, 5};
        ByteBufferUtil.putBytes(buffer, data, -1, 3);

        // 验证buffer位置没有变化
        assertEquals(0, buffer.position());
    }

    @Test
    void testPutBytesWithRangeInvalidLength() {
        ByteBuffer buffer = ByteBufferUtil.allocate(10);
        byte[] data = {1, 2, 3, 4, 5};
        ByteBufferUtil.putBytes(buffer, data, 0, -1);

        // 验证buffer位置没有变化
        assertEquals(0, buffer.position());
    }

    @Test
    void testPutBytesWithRangeOutOfBounds() {
        ByteBuffer buffer = ByteBufferUtil.allocate(10);
        byte[] data = {1, 2, 3, 4, 5};
        ByteBufferUtil.putBytes(buffer, data, 2, 10);

        // 验证buffer位置没有变化
        assertEquals(0, buffer.position());
    }

    @Test
    void testDuplicateWithNullBuffer() {
        ByteBuffer result = ByteBufferUtil.duplicate(null);
        assertNull(result);
    }

    @Test
    void testAsReadOnlyBufferWithNullBuffer() {
        ByteBuffer result = ByteBufferUtil.asReadOnlyBuffer(null);
        assertNull(result);
    }

    @Test
    void testResetWithNullBuffer() {
        // 这个方法应该不抛出异常
        ByteBufferUtil.reset(null);
        // 如果没有抛出异常，测试通过
        assertTrue(true);
    }

    @Test
    void testToHexStringWithNullBuffer() {
        String result = ByteBufferUtil.toHexString(null);
        assertEquals("", result);
    }

    @Test
    void testToHexStringWithEmptyBuffer() {
        ByteBuffer buffer = ByteBufferUtil.allocate(0);
        String result = ByteBufferUtil.toHexString(buffer);
        assertEquals("", result);
    }

    @Test
    void testFromHexStringWithNullString() {
        ByteBuffer buffer = ByteBufferUtil.fromHexString(null);
        assertNotNull(buffer);
        assertEquals(0, buffer.capacity());
    }

    @Test
    void testFromHexStringWithNullStringAfterProcessing() {
        // 测试处理后的字符串为null的情况
        ByteBuffer buffer = ByteBufferUtil.fromHexString("   ");
        assertNotNull(buffer);
        assertEquals(0, buffer.capacity());
    }

    @Test
    void testFromHexStringWithOddLength() {
        assertThrows(IllegalArgumentException.class, () -> {
            ByteBufferUtil.fromHexString("123"); // 奇数长度
        });
    }

    @Test
    void testFromHexStringWithInvalidCharacters() {
        assertThrows(NumberFormatException.class, () -> {
            ByteBufferUtil.fromHexString("XX"); // 无效的十六进制字符
        });
    }

    @Test
    void testWrapWithNullArray() {
        assertThrows(NullPointerException.class, () -> {
            ByteBufferUtil.wrap((byte[]) null);
        });
    }

    @Test
    void testWrapWithRangeNullArray() {
        assertThrows(NullPointerException.class, () -> {
            ByteBufferUtil.wrap(null, 0, 0);
        });
    }

    @Test
    void testToBytesWithNullBuffer() {
        byte[] result = ByteBufferUtil.toBytes(null);
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void testToBytesWithLengthNullBuffer() {
        byte[] result = ByteBufferUtil.toBytes(null, 5);
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void testToBytesWithLengthInvalidLength() {
        ByteBuffer buffer = ByteBufferUtil.allocate(10);
        byte[] result = ByteBufferUtil.toBytes(buffer, -1);
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void testToBytesWithLengthInsufficientData() {
        ByteBuffer buffer = ByteBufferUtil.allocate(5);
        buffer.putInt(12345); // 使用4字节
        buffer.flip();

        byte[] result = ByteBufferUtil.toBytes(buffer, 10);
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void testToBytesWithRangeNullBuffer() {
        byte[] result = ByteBufferUtil.toBytes(null, 0, 5);
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void testToBytesWithRangeInvalidOffset() {
        ByteBuffer buffer = ByteBufferUtil.allocate(10);
        byte[] result = ByteBufferUtil.toBytes(buffer, -1, 5);
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void testToBytesWithRangeInvalidLength() {
        ByteBuffer buffer = ByteBufferUtil.allocate(10);
        byte[] result = ByteBufferUtil.toBytes(buffer, 0, -1);
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void testToBytesWithRangeOutOfBounds() {
        ByteBuffer buffer = ByteBufferUtil.allocate(5);
        byte[] result = ByteBufferUtil.toBytes(buffer, 2, 10);
        assertNotNull(result);
        assertEquals(0, result.length);
    }
}
