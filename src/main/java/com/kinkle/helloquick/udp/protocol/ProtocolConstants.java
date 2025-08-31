package com.kinkle.helloquick.udp.protocol;

/**
 * UDP协议常量定义
 * 
 * <p>包含协议相关的所有常量定义，如魔数、包头长度、协议号等。</p>
 * 
 * @author kinkle
 * @version 1.0
 * @since 2025-08-31
 */
public final class ProtocolConstants {
    
    /**
     * 协议魔数，用于协议识别
     */
    public static final int MAGIC_NUMBER = 0x12345678;
    
    /**
     * 协议版本号
     */
    public static final byte PROTOCOL_VERSION = 0x01;
    
    /**
     * 包头固定长度（字节）
     */
    public static final int HEADER_LENGTH = 25;
    
    /**
     * 最大包体长度（字节）
     */
    public static final int MAX_BODY_LENGTH = 64 * 1024; // 64KB
    
    /**
     * 最小包体长度（字节）
     */
    public static final int MIN_BODY_LENGTH = 0;
    
    /**
     * 包头字段偏移量定义
     */
    public static final class HeaderOffsets {
        /** 魔数偏移量 */
        public static final int MAGIC = 0;
        /** 版本号偏移量 */
        public static final int VERSION = 4;
        /** 协议号偏移量 */
        public static final int PROTOCOL_ID = 5;
        /** 编码格式偏移量 */
        public static final int ENCODING = 7;
        /** 加密方式偏移量 */
        public static final int ENCRYPTION = 8;
        /** 包体长度偏移量 */
        public static final int BODY_LENGTH = 9;
        /** 时间戳偏移量 */
        public static final int TIMESTAMP = 13;
        /** 保留字段偏移量 */
        public static final int RESERVED = 21;
    }
    
    /**
     * 包头字段长度定义
     */
    public static final class HeaderLengths {
        /** 魔数长度 */
        public static final int MAGIC = 4;
        /** 版本号长度 */
        public static final int VERSION = 1;
        /** 协议号长度 */
        public static final int PROTOCOL_ID = 2;
        /** 编码格式长度 */
        public static final int ENCODING = 1;
        /** 加密方式长度 */
        public static final int ENCRYPTION = 1;
        /** 包体长度字段长度 */
        public static final int BODY_LENGTH = 4;
        /** CRC32长度 */
        public static final int CRC32 = 4;
        /** 时间戳长度 */
        public static final int TIMESTAMP = 8;
        /** 保留字段长度 */
        public static final int RESERVED = 4;
    }
    
    /**
     * 编码格式定义
     */
    public static final class Encoding {
        /** JSON格式 */
        public static final byte JSON = 0x00;
        /** YAML格式 */
        public static final byte YAML = 0x01;
        /** XML格式 */
        public static final byte XML = 0x02;
        /** 二进制格式 */
        public static final byte BINARY = 0x03;
    }
    
    /**
     * 加密方式定义
     */
    public static final class Encryption {
        /** 无加密 */
        public static final byte NONE = 0x00;
        /** DTLS加密 */
        public static final byte DTLS = 0x01;
        /** AES加密 */
        public static final byte AES = 0x02;
    }
    
    /**
     * 协议号范围定义
     */
    public static final class ProtocolRanges {
        /** 系统协议起始号 */
        public static final short SYSTEM_START = 0x0001;
        /** 系统协议结束号 */
        public static final short SYSTEM_END = 0x00FF;
        /** 业务协议起始号 */
        public static final short BUSINESS_START = 0x0100;
        /** 业务协议结束号 */
        public static final short BUSINESS_END = 0x0FFF;
        /** 扩展协议起始号 */
        public static final short EXTENSION_START = 0x1000;
        /** 扩展协议结束号 */
        public static final short EXTENSION_END = (short) 0x7FFF;
    }
    
    /**
     * 系统协议号定义
     */
    public static final class SystemProtocols {
        /** 心跳检测 */
        public static final short HEARTBEAT = 0x0001;
        /** 认证请求 */
        public static final short AUTH_REQUEST = 0x0002;
        /** 认证响应 */
        public static final short AUTH_RESPONSE = 0x0003;
        /** 错误响应 */
        public static final short ERROR_RESPONSE = 0x0004;
        /** 确认响应 */
        public static final short ACK = 0x0005;
    }
    
    /**
     * 业务协议号定义
     */
    public static final class BusinessProtocols {
        /** 用户登录 */
        public static final short USER_LOGIN = 0x0100;
        /** 用户登出 */
        public static final short USER_LOGOUT = 0x0101;
        /** 数据同步 */
        public static final short DATA_SYNC = 0x0102;
        /** 文件上传 */
        public static final short FILE_UPLOAD = 0x0103;
    }
    
    /**
     * 错误码定义
     */
    public static final class ErrorCodes {
        /** 成功 */
        public static final int SUCCESS = 0x0000;
        /** 协议错误 */
        public static final int PROTOCOL_ERROR = 0x0001;
        /** 认证失败 */
        public static final int AUTH_FAILED = 0x0002;
        /** 参数错误 */
        public static final int INVALID_PARAM = 0x0003;
        /** 系统错误 */
        public static final int SYSTEM_ERROR = 0x0004;
        /** 超时错误 */
        public static final int TIMEOUT = 0x0005;
    }
    
    /**
     * 私有构造函数，防止实例化
     */
    private ProtocolConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
}
