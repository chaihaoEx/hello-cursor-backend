package com.kinkle.helloquick.udp.util;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

/**
 * CRC32校验工具类
 * 
 * <p>提供CRC32校验相关的工具方法，用于数据完整性验证。</p>
 * 
 * @author kinkle
 * @version 1.0
 * @since 2025-08-31
 */
public final class Crc32Util {
    
    /**
     * 私有构造函数，防止实例化
     */
    private Crc32Util() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * 计算字节数组的CRC32值
     * 
     * @param data 字节数组
     * @return CRC32值
     */
    public static long calculate(byte[] data) {
        if (data == null || data.length == 0) {
            return 0;
        }
        
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        return crc32.getValue();
    }
    
    /**
     * 计算字节数组指定范围的CRC32值
     * 
     * @param data 字节数组
     * @param offset 起始偏移量
     * @param length 长度
     * @return CRC32值
     */
    public static long calculate(byte[] data, int offset, int length) {
        if (data == null || data.length == 0 || offset < 0 || length <= 0 || offset + length > data.length) {
            return 0;
        }
        
        CRC32 crc32 = new CRC32();
        crc32.update(data, offset, length);
        return crc32.getValue();
    }
    
    /**
     * 计算ByteBuffer的CRC32值
     * 
     * @param buffer ByteBuffer
     * @return CRC32值
     */
    public static long calculate(ByteBuffer buffer) {
        if (buffer == null || !buffer.hasRemaining()) {
            return 0;
        }
        
        CRC32 crc32 = new CRC32();
        crc32.update(buffer);
        return crc32.getValue();
    }
    
    /**
     * 计算ByteBuffer指定范围的CRC32值
     * 
     * @param buffer ByteBuffer
     * @param offset 起始偏移量
     * @param length 长度
     * @return CRC32值
     */
    public static long calculate(ByteBuffer buffer, int offset, int length) {
        if (buffer == null || offset < 0 || length <= 0 || offset + length > buffer.capacity()) {
            return 0;
        }
        
        int oldPosition = buffer.position();
        int oldLimit = buffer.limit();
        
        try {
            buffer.position(offset);
            buffer.limit(offset + length);
            
            CRC32 crc32 = new CRC32();
            crc32.update(buffer);
            return crc32.getValue();
        } finally {
            buffer.position(oldPosition);
            buffer.limit(oldLimit);
        }
    }
    
    /**
     * 计算包头和包体的CRC32值
     * 
     * @param header 包头数据
     * @param body 包体数据
     * @return CRC32值
     */
    public static long calculate(byte[] header, byte[] body) {
        CRC32 crc32 = new CRC32();
        
        if (header != null && header.length > 0) {
            crc32.update(header);
        }
        
        if (body != null && body.length > 0) {
            crc32.update(body);
        }
        
        return crc32.getValue();
    }
    
    /**
     * 验证CRC32值
     * 
     * @param data 数据
     * @param expectedCrc32 期望的CRC32值
     * @return 是否匹配
     */
    public static boolean verify(byte[] data, long expectedCrc32) {
        long actualCrc32 = calculate(data);
        return actualCrc32 == expectedCrc32;
    }
    
    /**
     * 验证CRC32值（指定范围）
     * 
     * @param data 数据
     * @param offset 起始偏移量
     * @param length 长度
     * @param expectedCrc32 期望的CRC32值
     * @return 是否匹配
     */
    public static boolean verify(byte[] data, int offset, int length, long expectedCrc32) {
        long actualCrc32 = calculate(data, offset, length);
        return actualCrc32 == expectedCrc32;
    }
    
    /**
     * 验证ByteBuffer的CRC32值
     * 
     * @param buffer ByteBuffer
     * @param expectedCrc32 期望的CRC32值
     * @return 是否匹配
     */
    public static boolean verify(ByteBuffer buffer, long expectedCrc32) {
        long actualCrc32 = calculate(buffer);
        return actualCrc32 == expectedCrc32;
    }
    
    /**
     * 验证包头和包体的CRC32值
     * 
     * @param header 包头数据
     * @param body 包体数据
     * @param expectedCrc32 期望的CRC32值
     * @return 是否匹配
     */
    public static boolean verify(byte[] header, byte[] body, long expectedCrc32) {
        long actualCrc32 = calculate(header, body);
        return actualCrc32 == expectedCrc32;
    }
    
    /**
     * 将CRC32值转换为字节数组
     * 
     * @param crc32 CRC32值
     * @return 4字节的字节数组
     */
    public static byte[] toBytes(long crc32) {
        return ByteBuffer.allocate(4).putInt((int) crc32).array();
    }
    
    /**
     * 从字节数组读取CRC32值
     * 
     * @param data 字节数组
     * @param offset 偏移量
     * @return CRC32值
     */
    public static long fromBytes(byte[] data, int offset) {
        if (data == null || offset < 0 || offset + 4 > data.length) {
            return 0;
        }
        
        return ByteBuffer.wrap(data, offset, 4).getInt() & 0xFFFFFFFFL;
    }
    
    /**
     * 计算并返回CRC32的字节数组形式
     * 
     * @param data 数据
     * @return CRC32的字节数组
     */
    public static byte[] calculateAsBytes(byte[] data) {
        long crc32 = calculate(data);
        return toBytes(crc32);
    }
    
    /**
     * 计算并返回CRC32的字节数组形式（指定范围）
     * 
     * @param data 数据
     * @param offset 起始偏移量
     * @param length 长度
     * @return CRC32的字节数组
     */
    public static byte[] calculateAsBytes(byte[] data, int offset, int length) {
        long crc32 = calculate(data, offset, length);
        return toBytes(crc32);
    }
}
