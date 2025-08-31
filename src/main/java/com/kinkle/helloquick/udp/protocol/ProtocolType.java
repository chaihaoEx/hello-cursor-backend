package com.kinkle.helloquick.udp.protocol;

/**
 * 协议类型枚举
 * 
 * <p>定义所有支持的协议类型，包括系统协议和业务协议。</p>
 * 
 * @author kinkle
 * @version 1.0
 * @since 2025-08-31
 */
public enum ProtocolType {
    
    // ========== 系统协议 ==========
    
    /**
     * 心跳检测协议
     */
    HEARTBEAT(ProtocolConstants.SystemProtocols.HEARTBEAT, "心跳检测", ProtocolCategory.SYSTEM),
    
    /**
     * 认证请求协议
     */
    AUTH_REQUEST(ProtocolConstants.SystemProtocols.AUTH_REQUEST, "认证请求", ProtocolCategory.SYSTEM),
    
    /**
     * 认证响应协议
     */
    AUTH_RESPONSE(ProtocolConstants.SystemProtocols.AUTH_RESPONSE, "认证响应", ProtocolCategory.SYSTEM),
    
    /**
     * 错误响应协议
     */
    ERROR_RESPONSE(ProtocolConstants.SystemProtocols.ERROR_RESPONSE, "错误响应", ProtocolCategory.SYSTEM),
    
    /**
     * 确认响应协议
     */
    ACK(ProtocolConstants.SystemProtocols.ACK, "确认响应", ProtocolCategory.SYSTEM),
    
    // ========== 业务协议 ==========
    
    /**
     * 用户登录协议
     */
    USER_LOGIN(ProtocolConstants.BusinessProtocols.USER_LOGIN, "用户登录", ProtocolCategory.BUSINESS),
    
    /**
     * 用户登出协议
     */
    USER_LOGOUT(ProtocolConstants.BusinessProtocols.USER_LOGOUT, "用户登出", ProtocolCategory.BUSINESS),
    
    /**
     * 数据同步协议
     */
    DATA_SYNC(ProtocolConstants.BusinessProtocols.DATA_SYNC, "数据同步", ProtocolCategory.BUSINESS),
    
    /**
     * 文件上传协议
     */
    FILE_UPLOAD(ProtocolConstants.BusinessProtocols.FILE_UPLOAD, "文件上传", ProtocolCategory.BUSINESS);
    
    /**
     * 协议号
     */
    private final short protocolId;
    
    /**
     * 协议名称
     */
    private final String name;
    
    /**
     * 协议分类
     */
    private final ProtocolCategory category;
    
    /**
     * 构造函数
     * 
     * @param protocolId 协议号
     * @param name 协议名称
     * @param category 协议分类
     */
    ProtocolType(short protocolId, String name, ProtocolCategory category) {
        this.protocolId = protocolId;
        this.name = name;
        this.category = category;
    }
    
    /**
     * 获取协议号
     * 
     * @return 协议号
     */
    public short getProtocolId() {
        return protocolId;
    }
    
    /**
     * 获取协议名称
     * 
     * @return 协议名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取协议分类
     * 
     * @return 协议分类
     */
    public ProtocolCategory getCategory() {
        return category;
    }
    
    /**
     * 根据协议号获取协议类型
     * 
     * @param protocolId 协议号
     * @return 协议类型，如果未找到则返回null
     */
    public static ProtocolType fromProtocolId(short protocolId) {
        for (ProtocolType type : values()) {
            if (type.protocolId == protocolId) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * 检查是否为系统协议
     * 
     * @param protocolId 协议号
     * @return 是否为系统协议
     */
    public static boolean isSystemProtocol(short protocolId) {
        return protocolId >= ProtocolConstants.ProtocolRanges.SYSTEM_START 
            && protocolId <= ProtocolConstants.ProtocolRanges.SYSTEM_END;
    }
    
    /**
     * 检查是否为业务协议
     * 
     * @param protocolId 协议号
     * @return 是否为业务协议
     */
    public static boolean isBusinessProtocol(short protocolId) {
        return protocolId >= ProtocolConstants.ProtocolRanges.BUSINESS_START 
            && protocolId <= ProtocolConstants.ProtocolRanges.BUSINESS_END;
    }
    
    /**
     * 检查是否为扩展协议
     * 
     * @param protocolId 协议号
     * @return 是否为扩展协议
     */
    public static boolean isExtensionProtocol(short protocolId) {
        return protocolId >= ProtocolConstants.ProtocolRanges.EXTENSION_START 
            && protocolId <= ProtocolConstants.ProtocolRanges.EXTENSION_END;
    }
    
    /**
     * 协议分类枚举
     */
    public enum ProtocolCategory {
        /** 系统协议 */
        SYSTEM("系统协议"),
        /** 业务协议 */
        BUSINESS("业务协议"),
        /** 扩展协议 */
        EXTENSION("扩展协议");
        
        private final String description;
        
        ProtocolCategory(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
