package com.kinkle.helloquick.udp.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 字节缓冲区工具类
 * 
 * <p>提供字节缓冲区操作相关的工具方法，包括创建、读写、转换等。</p>
 * 
 * @author kinkle
 * @version 1.0
 * @since 2025-08-31
 */
public final class ByteBufferUtil {
    
    /**
     * 默认字节序
     */
    private static final ByteOrder DEFAULT_ORDER = ByteOrder.BIG_ENDIAN;
    
    /**
     * 私有构造函数，防止实例化
     */
    private ByteBufferUtil() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * 创建指定容量的ByteBuffer
     * 
     * @param capacity 容量
     * @return ByteBuffer
     */
    public static ByteBuffer allocate(int capacity) {
        return ByteBuffer.allocate(capacity).order(DEFAULT_ORDER);
    }
    
    /**
     * 创建指定容量的直接ByteBuffer
     * 
     * @param capacity 容量
     * @return 直接ByteBuffer
     */
    public static ByteBuffer allocateDirect(int capacity) {
        return ByteBuffer.allocateDirect(capacity).order(DEFAULT_ORDER);
    }
    
    /**
     * 包装字节数组为ByteBuffer
     * 
     * @param array 字节数组
     * @return ByteBuffer
     */
    public static ByteBuffer wrap(byte[] array) {
        if (array == null) {
            throw new NullPointerException("Array cannot be null");
        }
        return ByteBuffer.wrap(array).order(DEFAULT_ORDER);
    }
    
    /**
     * 包装字节数组的指定范围为ByteBuffer
     * 
     * @param array 字节数组
     * @param offset 偏移量
     * @param length 长度
     * @return ByteBuffer
     */
    public static ByteBuffer wrap(byte[] array, int offset, int length) {
        if (array == null) {
            throw new NullPointerException("Array cannot be null");
        }
        return ByteBuffer.wrap(array, offset, length).order(DEFAULT_ORDER);
    }
    
    /**
     * 从ByteBuffer读取字节数组
     * 
     * @param buffer ByteBuffer
     * @return 字节数组
     */
    public static byte[] toBytes(ByteBuffer buffer) {
        if (buffer == null) {
            return new byte[0];
        }
        
        // 重置position到0，读取所有数据
        buffer.flip();
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }
    
    /**
     * 从ByteBuffer读取指定长度的字节数组
     * 
     * @param buffer ByteBuffer
     * @param length 长度
     * @return 字节数组
     */
    public static byte[] toBytes(ByteBuffer buffer, int length) {
        if (buffer == null || length <= 0 || buffer.remaining() < length) {
            return new byte[0];
        }
        
        byte[] result = new byte[length];
        buffer.get(result);
        return result;
    }
    
    /**
     * 从ByteBuffer读取指定范围的字节数组
     * 
     * @param buffer ByteBuffer
     * @param offset 偏移量
     * @param length 长度
     * @return 字节数组
     */
    public static byte[] toBytes(ByteBuffer buffer, int offset, int length) {
        if (buffer == null || offset < 0 || length <= 0 || offset + length > buffer.capacity()) {
            return new byte[0];
        }
        
        int oldPosition = buffer.position();
        int oldLimit = buffer.limit();
        
        try {
            buffer.position(offset);
            buffer.limit(offset + length);
            
            byte[] result = new byte[length];
            buffer.get(result);
            return result;
        } finally {
            buffer.position(oldPosition);
            buffer.limit(oldLimit);
        }
    }
    
    /**
     * 将int值写入ByteBuffer
     * 
     * @param buffer ByteBuffer
     * @param value int值
     */
    public static void putInt(ByteBuffer buffer, int value) {
        buffer.putInt(value);
    }
    
    /**
     * 从ByteBuffer读取int值
     * 
     * @param buffer ByteBuffer
     * @return int值
     */
    public static int getInt(ByteBuffer buffer) {
        return buffer.getInt();
    }
    
    /**
     * 将long值写入ByteBuffer
     * 
     * @param buffer ByteBuffer
     * @param value long值
     */
    public static void putLong(ByteBuffer buffer, long value) {
        buffer.putLong(value);
    }
    
    /**
     * 从ByteBuffer读取long值
     * 
     * @param buffer ByteBuffer
     * @return long值
     */
    public static long getLong(ByteBuffer buffer) {
        return buffer.getLong();
    }
    
    /**
     * 将short值写入ByteBuffer
     * 
     * @param buffer ByteBuffer
     * @param value short值
     */
    public static void putShort(ByteBuffer buffer, short value) {
        buffer.putShort(value);
    }
    
    /**
     * 从ByteBuffer读取short值
     * 
     * @param buffer ByteBuffer
     * @return short值
     */
    public static short getShort(ByteBuffer buffer) {
        return buffer.getShort();
    }
    
    /**
     * 将byte值写入ByteBuffer
     * 
     * @param buffer ByteBuffer
     * @param value byte值
     */
    public static void putByte(ByteBuffer buffer, byte value) {
        buffer.put(value);
    }
    
    /**
     * 从ByteBuffer读取byte值
     * 
     * @param buffer ByteBuffer
     * @return byte值
     */
    public static byte getByte(ByteBuffer buffer) {
        return buffer.get();
    }
    
    /**
     * 将字节数组写入ByteBuffer
     * 
     * @param buffer ByteBuffer
     * @param data 字节数组
     */
    public static void putBytes(ByteBuffer buffer, byte[] data) {
        if (data != null && data.length > 0) {
            buffer.put(data);
        }
    }
    
    /**
     * 将字节数组的指定范围写入ByteBuffer
     * 
     * @param buffer ByteBuffer
     * @param data 字节数组
     * @param offset 偏移量
     * @param length 长度
     */
    public static void putBytes(ByteBuffer buffer, byte[] data, int offset, int length) {
        if (data != null && data.length > 0 && offset >= 0 && length > 0 && offset + length <= data.length) {
            buffer.put(data, offset, length);
        }
    }
    
    /**
     * 复制ByteBuffer
     * 
     * @param source 源ByteBuffer
     * @return 复制的ByteBuffer
     */
    public static ByteBuffer duplicate(ByteBuffer source) {
        if (source == null) {
            return null;
        }
        
        ByteBuffer duplicate = source.duplicate();
        duplicate.order(source.order());
        return duplicate;
    }
    
    /**
     * 创建ByteBuffer的只读视图
     * 
     * @param source 源ByteBuffer
     * @return 只读ByteBuffer
     */
    public static ByteBuffer asReadOnlyBuffer(ByteBuffer source) {
        if (source == null) {
            return null;
        }
        
        ByteBuffer readOnly = source.asReadOnlyBuffer();
        readOnly.order(source.order());
        return readOnly;
    }
    
    /**
     * 获取ByteBuffer的剩余字节数
     * 
     * @param buffer ByteBuffer
     * @return 剩余字节数
     */
    public static int remaining(ByteBuffer buffer) {
        return buffer != null ? buffer.remaining() : 0;
    }
    
    /**
     * 检查ByteBuffer是否有剩余数据
     * 
     * @param buffer ByteBuffer
     * @return 是否有剩余数据
     */
    public static boolean hasRemaining(ByteBuffer buffer) {
        return buffer != null && buffer.hasRemaining();
    }
    
    /**
     * 重置ByteBuffer的position和limit
     * 
     * @param buffer ByteBuffer
     */
    public static void reset(ByteBuffer buffer) {
        if (buffer != null) {
            buffer.position(0);
            buffer.limit(buffer.capacity());
        }
    }
    
    /**
     * 将ByteBuffer转换为十六进制字符串
     * 
     * @param buffer ByteBuffer
     * @return 十六进制字符串
     */
    public static String toHexString(ByteBuffer buffer) {
        if (buffer == null || !buffer.hasRemaining()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        int oldPosition = buffer.position();
        
        try {
            while (buffer.hasRemaining()) {
                byte b = buffer.get();
                sb.append(String.format("%02X", b));
            }
        } finally {
            buffer.position(oldPosition);
        }
        
        return sb.toString();
    }
    
    /**
     * 将十六进制字符串转换为ByteBuffer
     * 
     * @param hexString 十六进制字符串
     * @return ByteBuffer
     */
    public static ByteBuffer fromHexString(String hexString) {
        if (hexString == null || hexString.isEmpty()) {
            return allocate(0);
        }
        
        // 移除空格和0x前缀
        hexString = hexString.replaceAll("\\s+", "").replaceAll("0x", "");
        
        if (hexString.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex string length");
        }
        
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16);
        }
        
        return wrap(bytes);
    }
}
